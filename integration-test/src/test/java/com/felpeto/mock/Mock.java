package com.felpeto.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;

public class Mock {

  public static void mockQuotation(final WireMockServer mockServer) {
    mockServer.stubFor(get(urlPathMatching("/v1/accounting/od/rates_of_exchange"))
        .willReturn(aResponse()
            .withBodyFile("fiscalDataResponse.json")
            .withStatus(200)
            .withHeader("Content-Type", "application/json")));
  }

  public static void mockQuotationBadRequest(final WireMockServer mockServer) {
    mockServer.stubFor(get(urlPathMatching("/v1/accounting/od/rates_of_exchange"))
        .willReturn(aResponse()
            .withBodyFile("fiscalDataResponse.json")
            .withStatus(401)
            .withHeader("Content-Type", "application/json")));
  }

  public static void resetAll(final WireMockServer mockServer) {
      mockServer.resetAll();
  }
}
