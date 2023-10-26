package com.felpeto.purchase.client;

import com.felpeto.purchase.client.dto.CurrencyExchangeResponseDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.time.temporal.ChronoUnit;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "fiscal-data")
public interface FiscalData {

  @GET
  @Path("v1/accounting/od/rates_of_exchange")
  @Timeout(unit = ChronoUnit.SECONDS, value = 2)
  @CircuitBreaker(
      requestVolumeThreshold = 4,
      failureRatio = 0.5,
      delay = 5000,
      successThreshold = 2
  )
  CurrencyExchangeResponseDto getCurrencyRate(@QueryParam("fields") String fields,
      @QueryParam("filter") String filter,
      @QueryParam("sort") String sort);
}
