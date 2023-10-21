package com.felpeto.purchase.controller.handler;

import static jakarta.ws.rs.core.Response.Status.fromStatusCode;

import com.felpeto.purchase.client.handler.exception.ClientException;
import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.util.Set;

@Provider
public class ClientExceptionMapper implements
    ExceptionMapper<ClientException> {


  @Override
  public Response toResponse(final ClientException exception) {
    final var details = Set.of(
        ErrorDetails.builder()
            .target(exception.getTarget())
            .violationMessage(exception.getMessage())
            .build()
    );

    final ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
        .code(fromStatusCode(exception.getStatus()).name())
        .details(details)
        .message(exception.getMessage())
        .status(exception.getStatus())
        .timestamp(LocalDateTime.now())
        .build();

    return Response.status(exception.getStatus())
        .entity(errorResponseDto)
        .build();
  }
}
