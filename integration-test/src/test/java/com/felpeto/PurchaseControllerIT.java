package com.felpeto;

import static com.felpeto.mock.Mock.mockQuotation;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.felpeto.purchase.controller.dto.response.PurchaseResponseDto;
import io.restassured.http.ContentType;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

public class PurchaseControllerIT extends AbstractContainerTest {
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
        .extract()
        .as(PurchaseResponseDto.class);

    assertThat(response, notNullValue());
    assertThat(response.getId(), notNullValue());
    assertThat(response.getAmount(), equalTo(purchaseRequest.getAmount().setScale(2, RoundingMode.HALF_UP)));
    assertThat(response.getDescription(), equalTo(purchaseRequest.getDescription()));
    assertThat(response.getTransactionDate(), equalTo(purchaseRequest.getTransactionDate()));
    assertThat(response.getExchangeRate(), nullValue());
    assertThat(response.getConvertedMoney(), nullValue());

    mockQuotation(MOCK_SERVER, response.getTransactionDate());

    given()
        .contentType(ContentType.JSON)
        .queryParam("country", "Brazil")
        .when()
        .get("/v1/purchase/{uuid}", response.getId().toString())
        .then()
        .statusCode(200);
  }
}
