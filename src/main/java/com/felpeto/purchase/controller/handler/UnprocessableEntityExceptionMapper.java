package com.felpeto.purchase.controller.handler;

import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import com.felpeto.purchase.model.exceptions.UnprocessableEntityException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.util.Set;

@Provider
public class UnprocessableEntityExceptionMapper implements
    ExceptionMapper<UnprocessableEntityException> {

  @Override
  public Response toResponse(final UnprocessableEntityException exception) {
    final var details = Set.of(
        ErrorDetails.builder()
            .field(exception.getField())
            .target(exception.getTarget())
            .violationMessage(exception.getViolationMessage())
            .build()
    );

    final ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
        .code("INVALID_FORMAT_EXCEPTION")
        .details(details)
        .message(exception.getMessage())
        .parameter(exception.getParameter())
        .status(422)
        .timestamp(LocalDateTime.now())
        .build();

    return Response.status(422)
        .entity(errorResponseDto)
        .build();
  }
}
