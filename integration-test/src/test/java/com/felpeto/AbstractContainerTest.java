package com.felpeto;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import java.math.BigDecimal;
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

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final Faker faker = new Faker();
  private static final GenericContainer<?> APP;
  private static final GenericContainer<?> FLYWAY;
  private static final GenericContainer<?> MYSQL_CONTAINER;
  private static final Network NETWORK = Network.newNetwork();
  protected static final WireMockServer MOCK_SERVER;

  static {
    MOCK_SERVER = new WireMockServer(wireMockConfig().dynamicPort());
    MOCK_SERVER.start();
    exposeHostMachinePortToContainersForApiIntegrations();

    MYSQL_CONTAINER = buildMySqlContainer();
    MYSQL_CONTAINER.start();

    FLYWAY = buildFlywayContainer(MYSQL_CONTAINER);
    FLYWAY.start();

    APP = buildAppContainer(MYSQL_CONTAINER, FLYWAY);
    APP.start();
    System.out.println(APP.getHost() + ":" + APP.getFirstMappedPort());
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    initRestAssured();
  }

  private static void exposeHostMachinePortToContainersForApiIntegrations() {
    Testcontainers.exposeHostPorts(MOCK_SERVER.port());
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
        .withCommand("-url=jdbc:mysql://testdb?useSSL=false -schemas=wex -user=test -password=test -connectRetries=60 migrate")
        .waitingFor(Wait.forLogMessage("(?s).*Successfully applied(?s).*", 1))
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("FLYWAY")));
  }

  private static GenericContainer<?> buildAppContainer(final Startable... dependsOn) {
    return new GenericContainer<>("app-test:integration")
        .dependsOn(dependsOn)
        .withNetwork(NETWORK)
        .withEnv("FISCAL_DATA_URL", "http://host.testcontainers.internal:" + MOCK_SERVER.port())
        .withEnv("MYSQL_USER", "test")
        .withEnv("MYSQL_PASSWORD", "test")
        .withEnv("MYSQL_URL", "jdbc:mysql://testdb:" + MySQLContainer.MYSQL_PORT + "/wex?autoReconnect=true&useSSL=false")
        .withExposedPorts(8080)
        .waitingFor(Wait.forHttp("/q/health/ready").forStatusCode(200))
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("APP_CONTAINER")));
  }

  private static void initRestAssured() {
    RestAssured.baseURI = "http://" + APP.getHost();
    RestAssured.port = APP.getFirstMappedPort();
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    MAPPER.registerModule(new JavaTimeModule());
    MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
