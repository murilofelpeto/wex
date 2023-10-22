package com.felpeto.purchase.client;

import com.felpeto.purchase.client.dto.CurrencyExchangeResponseDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "fiscal-data")
public interface FiscalData {

  @GET
  @Path("v1/accounting/od/rates_of_exchange")
  CurrencyExchangeResponseDto getCurrencyRate(@QueryParam("fields") String fields,
      @QueryParam("filter") String filter,
      @QueryParam("sort") String sort);
}
