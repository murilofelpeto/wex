package com.felpeto.purchase.client.handler;

import com.felpeto.purchase.client.handler.exception.ClientBadRequestException;
import com.felpeto.purchase.client.handler.exception.ClientInternalServerErrorException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Slf4j
public class ClientExceptionHandler implements ResponseExceptionMapper<RuntimeException> {

  @Override
  public RuntimeException toThrowable(final Response response) {
    log.error("Unexpected error " + response.getEntity());
    if (response.getStatus() >= 400 && response.getStatus() <= 499) {
      throw new ClientBadRequestException("FiscalData",
          "Unexpected error",
          response.getStatus());
    }

    if (response.getStatus() >= 500 && response.getStatus() <= 599) {
      throw new ClientInternalServerErrorException("FiscalData",
          "Something went wrong, try again later",
          response.getStatus());
    }
    return null;
  }
}
