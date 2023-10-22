package com.felpeto.purchase.controller.handler;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.NoArgsConstructor;


@Provider
@NoArgsConstructor
public class ConstraintViolationExceptionMapper implements
    ExceptionMapper<ConstraintViolationException> {

  @Context
  private UriInfo uriInfo;

  public ConstraintViolationExceptionMapper(final UriInfo uriInfo) {
    this.uriInfo = uriInfo;
  }

  @Override
  public Response toResponse(final ConstraintViolationException exception) {
    final var constraintViolations = exception.getConstraintViolations();
    final var details = toErrorDetails(constraintViolations);

    final ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
        .code("REQUEST_FORMAT_EXCEPTION")
        .details(details)
        .message(exception.getMessage())
        .parameter(uriInfo.getAbsolutePath().getPath())
        .status(BAD_REQUEST.getStatusCode())
        .timestamp(LocalDateTime.now())
        .build();

    return Response.status(BAD_REQUEST).entity(errorResponseDto).build();
  }

  private Set<ErrorDetails> toErrorDetails(final Set<ConstraintViolation<?>> violations) {
    final var details = new HashSet<ErrorDetails>();

    for (final var violation : violations) {
      final var detail = ErrorDetails.builder()
          .field(violation.getPropertyPath().toString().split("\\.")[2])
          .target(violation.getLeafBean().toString().split("@")[0])
          .violationMessage(violation.getMessage())
          .build();

      details.add(detail);
    }

    return details;
  }
}
