package com.felpeto.purchase.controller.handler;

import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import com.felpeto.purchase.model.exceptions.ValueNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.util.Set;

@Provider
public class ValueNotFoundExceptionMapper implements ExceptionMapper<ValueNotFoundException> {

  @Override
  public Response toResponse(final ValueNotFoundException exception) {
    final var details = Set.of(
        ErrorDetails.builder()
            .field(exception.getField())
            .target(exception.getTarget())
            .violationMessage(exception.getViolationMessage())
            .build()
    );

    final ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
        .code("ENTITY_NOT_FOUND_EXCEPTION")
        .details(details)
        .message(exception.getMessage())
        .parameter(exception.getParameter())
        .status(404)
        .timestamp(LocalDateTime.now())
        .build();

    return Response.status(404)
        .entity(errorResponseDto)
        .build();
  }
}
