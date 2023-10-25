package com.felpeto;

import static com.felpeto.mock.Mock.mockQuotation;
import static com.felpeto.mock.Mock.resetAll;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.controller.dto.response.PurchaseResponseDto;
import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import java.math.RoundingMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class PurchaseControllerIT extends AbstractContainerTest {

  @AfterEach
  void cleanMocks() {
    resetAll(MOCK_SERVER);
  }
  @Test
  void givenValidRequestBodyWhenSavePurchaseThenReturnResponse() throws JsonProcessingException {
    final var purchaseRequest = buildDefaultRequest();
    final var response = given()
        .contentType(ContentType.JSON)
        .body(convertToString(purchaseRequest))
        .when()
        .post("/v1/purchase")
        .then()
        .statusCode(201)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsons/schemas/persistenceResponseSchema.json"))
        .extract()
        .as(PurchaseResponseDto.class);

    assertThat(response).isNotNull();
    assertThat(response.getId()).isNotNull();
    assertThat(response.getAmount()).isEqualTo(purchaseRequest.getAmount().setScale(2, RoundingMode.HALF_UP));
    assertThat(response.getDescription()).isEqualTo(purchaseRequest.getDescription());
    assertThat(response.getTransactionDate()).isEqualTo(purchaseRequest.getTransactionDate());
    assertThat(response.getExchangeRate()).isNull();
    assertThat(response.getConvertedMoney()).isNull();

    mockQuotation(MOCK_SERVER);

    final var persistedResponse = given()
        .contentType(ContentType.JSON)
        .queryParam("country", "Brazil")
        .when()
        .get("/v1/purchase/{uuid}", response.getId().toString())
        .then()
        .statusCode(200)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsons/schemas/getterResponseSchema.json"))
        .extract()
        .as(PurchaseResponseDto.class);

    assertThat(persistedResponse).isNotNull();
    assertThat(persistedResponse.getId()).isEqualTo(response.getId());
    assertThat(persistedResponse.getExchangeRate()).isNotNull();
    assertThat(persistedResponse.getConvertedMoney()).isNotNull();
  }

  @Test
  void givenInvalidRequestBodyWhenSaveThenReturnConstraintException() {
    final var response = given()
        .contentType(ContentType.JSON)
        .body("{}")
        .when()
        .post("/v1/purchase")
        .then()
        .statusCode(400)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsons/schemas/violationSchema.json"))
        .extract()
        .as(ErrorResponseDto.class);

    final var detailAmount = ErrorDetails.builder()
        .target("PurchaseRequestDto(description=null, transactionDate=null, amount=null)")
        .field("amount")
        .violationMessage("Amount cannot be null")
        .build();

    final var detailDescription = ErrorDetails.builder()
        .target("PurchaseRequestDto(description=null, transactionDate=null, amount=null)")
        .field("description")
        .violationMessage("Description cannot be blank")
        .build();

    final var detailTransactionDate = ErrorDetails.builder()
        .target("PurchaseRequestDto(description=null, transactionDate=null, amount=null)")
        .field("transactionDate")
        .violationMessage("Transaction date cannot be null")
        .build();

    assertThat(response).isNotNull();
    assertThat(response.getCode()).isEqualTo("REQUEST_FORMAT_EXCEPTION");
    assertThat(response.getMessage()).isNotNull();
    assertThat(response.getParameter()).isEqualTo("/v1/purchase");
    assertThat(response.getTimestamp()).isNotNull();
    assertThat(response.getDetails())
        .hasSize(3)
        .containsExactlyInAnyOrder(detailAmount, detailDescription, detailTransactionDate);
  }

  @Test
  void givenNonexistentPurchaseWhenSaveThenReturnEntityNotFound() {
    final var response = given()
        .contentType(ContentType.JSON)
        .queryParam("country", "Brazil")
        .when()
        .get("/v1/purchase/{uuid}", "a93df063-6749-4e35-9aac-c31f4fe83a7f")
        .then()
        .statusCode(404)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsons/schemas/violationSchema.json"))
        .extract()
        .as(ErrorResponseDto.class);

    final var detail = ErrorDetails.builder()
        .target("PurchaseRequestDto(description=null, transactionDate=null, amount=null)")
        .field("transactionDate")
        .violationMessage("Transaction date cannot be null")
        .build();

    assertThat(response).isNotNull();
    assertThat(response.getCode()).isEqualTo("ENTITY_NOT_FOUND_EXCEPTION");
    assertThat(response.getMessage()).isEqualTo("Purchase not found");
    assertThat(response.getParameter()).isEqualTo("/v1/purchase");
    assertThat(response.getTimestamp()).isNotNull();
    assertThat(response.getDetails())
        .hasSize(1)
        .containsExactlyInAnyOrder(detail);
  }

  @Test
  void givenValidRequestWhenGetQuotationThenClientReturnBadRequest() throws JsonProcessingException {
    final var purchaseRequest = buildDefaultRequest();
    final var purchase = persistPurchase(purchaseRequest);

    mockQuotation(MOCK_SERVER);
    final var response = given()
        .contentType(ContentType.JSON)
        .queryParam("country", "Brazil")
        .when()
        .get("/v1/purchase/{uuid}", purchase.getId().toString())
        .then()
        .statusCode(400)
//        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("jsons/schemas/getterResponseSchema.json"))
        .extract()
        .as(ErrorResponseDto.class);

    System.out.println(response);
  }

  private PurchaseResponseDto persistPurchase(final PurchaseRequestDto purchaseRequest) {
    return given()
        .contentType(ContentType.JSON)
        .body(convertToString(purchaseRequest))
        .when()
        .post("/v1/purchase")
        .then()
        .statusCode(201)
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(
            "jsons/schemas/persistenceResponseSchema.json"))
        .extract()
        .as(PurchaseResponseDto.class);
  }
}
