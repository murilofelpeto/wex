package com.felpeto.purchase.controller.handler;

import static jakarta.ws.rs.core.Response.Status.fromStatusCode;
import static org.assertj.core.api.Assertions.assertThat;

import com.felpeto.purchase.client.handler.exception.ClientBadRequestException;
import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ClientExceptionMapperTest {

  private final ClientExceptionMapper mapper = new ClientExceptionMapper();

  @Test
  void givenInvalidFormatExceptionWhenMapToResponseThenReturnBadRequest() {
    final var message = "Unexpected error";
    final var target = "FiscalData";
    final var status = 400;
    final var exception = new ClientBadRequestException(target,
        message,
        status);

    final var response = mapper.toResponse(exception);
    final var body = (ErrorResponseDto) response.getEntity();

    assertThat(response.getStatus()).isEqualTo(status);
    assertThat(body.getCode()).isEqualTo(fromStatusCode(exception.getStatus()).name());
    assertThat(body.getMessage()).isEqualTo(message);
    assertThat(body.getParameter()).isNull();
    assertThat(body.getStatus()).isEqualTo(status);
    assertThat(body.getTimestamp())
        .isBetween(LocalDateTime.now().minusSeconds(1), LocalDateTime.now());

    final ErrorDetails errorDetail = ErrorDetails.builder()
        .target(target)
        .violationMessage(message)
        .build();
    assertThat(body.getDetails()).hasSize(1).containsExactly(errorDetail);
  }
}