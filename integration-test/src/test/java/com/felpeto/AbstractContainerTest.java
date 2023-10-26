package com.felpeto;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.testcontainers.utility.MountableFile.forClasspathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.felpeto.auth.Authenticator;
import com.felpeto.container.AppContainer;
import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import org.slf4j.LoggerFactory;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.utility.MountableFile;

abstract class AbstractContainerTest {

  protected static final WireMockServer MOCK_SERVER;
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final GenericContainer<?> APP;
  private static final GenericContainer<?> FLYWAY;
  private static final GenericContainer<?> MYSQL_CONTAINER;
  private static final GenericContainer<?> KEYCLOAK_CONTAINER;
  private static final Network NETWORK = Network.newNetwork();

  static {
    MOCK_SERVER = new WireMockServer(wireMockConfig().dynamicPort());
    MOCK_SERVER.start();
    exposeHostMachinePortToContainersForApiIntegrations();

    MYSQL_CONTAINER = buildMySqlContainer();
    MYSQL_CONTAINER.start();

    FLYWAY = buildFlywayContainer(MYSQL_CONTAINER);
    FLYWAY.start();

    KEYCLOAK_CONTAINER = buildKeycloakContainer();
    KEYCLOAK_CONTAINER.start();

    APP = buildAppContainer(MYSQL_CONTAINER, FLYWAY, KEYCLOAK_CONTAINER);
    APP.start();

    initRestAssured();
  }

  private final Faker faker = new Faker();

  private static void exposeHostMachinePortToContainersForApiIntegrations() {
    Testcontainers.exposeHostPorts(MOCK_SERVER.port());
  }

  private static GenericContainer<?> buildKeycloakContainer() {
    return new GenericContainer("quay.io/keycloak/keycloak:20.0")
        .withNetwork(NETWORK)
        .withNetworkAliases("keycloak")
        .withCommand("start-dev --import-realm")
        .withCopyFileToContainer(forClasspathResource("keycloak"), "/opt/keycloak/data/import")
        .withEnv("KEYCLOAK_ADMIN", "admin")
        .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
        .waitingFor(Wait.forLogMessage(".*Running the server in development mode.*", 1))
        .withStartupTimeout(Duration.ofSeconds(120))
        .withExposedPorts(9002, 8080);
  }

  private static GenericContainer<?> buildMySqlContainer() {
    return new MySQLContainer<>("mysql:8.0")
        .withNetwork(NETWORK)
        .withDatabaseName("wex")
        .withUsername("test")
        .withPassword("test")
        .withNetworkAliases("testdb");
  }

  private static GenericContainer<?> buildFlywayContainer(final Startable... dependsOn) {
    return new GenericContainer<>("flyway/flyway:7.5.2")
        .dependsOn(dependsOn)
        .withNetwork(NETWORK)
        .withCopyFileToContainer(MountableFile.forHostPath("../resources/flyway/db"), "/flyway/sql")
        .withCommand(
            "-url=jdbc:mysql://testdb?useSSL=false -schemas=wex -user=test -password=test -connectRetries=60 migrate")
        .waitingFor(Wait.forLogMessage("(?s).*Successfully applied(?s).*", 1))
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("FLYWAY")));
  }

  private static GenericContainer<?> buildAppContainer(final Startable... dependsOn) {
    return new AppContainer()
        .dependsOn(dependsOn)
        .withNetwork(NETWORK)
        .withExposedPorts(8080)
        .withHostDebug(5005)
        .waitingFor(Wait.forHttp("/q/metrics").forStatusCode(200))
        .withEnv("QUARKUS_HTTP_PORT", "8080")
        .withEnv("KEYCLOAK_AUTH_URL", "http://keycloak:8080/realms/wex")
        .withEnv("KEYCLOAK_ADMIN", "admin")
        .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
        .withEnv("KEYCLOAK_CLIENT_ID", "backend")
        .withEnv("KEYCLOAK_SECRET", "FOJHmE8A3ckjM5mANHLBAE72PbFIyzED")
        .withEnv("QUARKUS_OIDC_ROLES_ROLE_CLAIM_PATH", "realm_access/roles")
        .withEnv("QUARKUS_OIDC_TOKEN_ISSUER", "any")
        .withEnv("FISCAL_DATA_URL", "http://host.testcontainers.internal:" + MOCK_SERVER.port())
        .withEnv("MYSQL_USER", "test")
        .withEnv("MYSQL_PASSWORD", "test")
        .withEnv("MYSQL_URL", "jdbc:mysql://testdb:" + MySQLContainer.MYSQL_PORT
            + "/wex?autoReconnect=true&useSSL=false")
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("APP_CONTAINER")));
  }

  private static void initRestAssured() {
    RestAssured.baseURI = "http://" + APP.getHost();
    RestAssured.port = APP.getFirstMappedPort();
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    MAPPER.registerModule(new JavaTimeModule());
    Authenticator.port = KEYCLOAK_CONTAINER.getMappedPort(8080);
  }

  PurchaseRequestDto buildDefaultRequest() throws JsonProcessingException {
    return PurchaseRequestDto.builder()
        .amount(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
        .description(faker.aviation().aircraft())
        .transactionDate(LocalDate.now())
        .build();
  }

  String convertToString(Object object) {
    try {
      return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
