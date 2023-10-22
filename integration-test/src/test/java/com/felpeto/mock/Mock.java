package com.felpeto.mock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.LocalDate;

public class Mock {

  public static void mockQuotation(final WireMockServer mockServer,
      final LocalDate transactionDate) {

    final var fields = "record_date, currency, exchange_rate";
    final var sort = "-record_date";

    final var filter = "country:eq:"
        + "Brazil"
        + ",record_date:lte:"
        + transactionDate;

    final var sb = new StringBuilder();
    sb.append("v1/accounting/od/rates_of_exchange");
    sb.append("?fields=");
    sb.append(fields);
    sb.append("&filter=");
    sb.append(filter);
    sb.append("&sort=");
    sb.append(sort);

    mockServer.stubFor(get(urlPathMatching(sb.toString()))
        .willReturn(aResponse()
            .withBodyFile("fiscalDataResponse.json")
            .withStatus(200)
            .withHeader("Content-Type", "application/json")));
  }


}
