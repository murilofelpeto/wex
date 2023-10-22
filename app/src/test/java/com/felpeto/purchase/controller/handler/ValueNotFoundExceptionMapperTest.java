package com.felpeto.purchase.controller.handler;

import static org.assertj.core.api.Assertions.assertThat;

import com.felpeto.purchase.controller.handler.dto.ErrorDetails;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import com.felpeto.purchase.model.exceptions.ValueNotFoundException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValueNotFoundExceptionMapperTest {

  private final ValueNotFoundExceptionMapper mapper = new ValueNotFoundExceptionMapper();

  @Test
  void givenInvalidFormatExceptionWhenMapToResponseThenReturnBadRequest() {
    final var message = "ValueNotFoundException";
    final var exception = new ValueNotFoundException(message,
        "parameter",
        "target",
        "field",
        "violation message");

    final var response = mapper.toResponse(exception);
    final var body = (ErrorResponseDto) response.getEntity();

    assertThat(response.getStatus()).isEqualTo(404);
    assertThat(body.getMessage()).isEqualTo(message);
    assertThat(body.getCode()).isEqualTo("ENTITY_NOT_FOUND_EXCEPTION");
    assertThat(body.getParameter()).isEqualTo("parameter");
    assertThat(body.getStatus()).isEqualTo(404);
    assertThat(body.getTimestamp())
        .isBetween(LocalDateTime.now().minusSeconds(1), LocalDateTime.now());

    final ErrorDetails errorDetail = ErrorDetails.builder()
        .field("field")
        .target("target")
        .violationMessage("violation message")
        .build();
    assertThat(body.getDetails()).hasSize(1).containsExactly(errorDetail);
  }

  @Test
  @DisplayName("Given InvalidPropertyNumberException when map toResponse then return BAD_REQUEST")
  void givenInvalidPropertyNumberExceptionWhenMapToResponseThenReturnBadRequest() {
    final var message = "ValueNotFoundException";
    final var exception = new ValueNotFoundException(message, "parameter", "target",
        "field",
        "violation message");

    final var response = mapper.toResponse(exception);
    final var body = (ErrorResponseDto) response.getEntity();

    assertThat(response.getStatus()).isEqualTo(404);
    assertThat(body.getMessage()).isEqualTo(message);
    assertThat(body.getCode()).isEqualTo("ENTITY_NOT_FOUND_EXCEPTION");
    assertThat(body.getParameter()).isEqualTo("parameter");
    assertThat(body.getStatus()).isEqualTo(404);
    assertThat(body.getTimestamp())
        .isBetween(LocalDateTime.now().minusSeconds(1), LocalDateTime.now());

    final ErrorDetails errorDetail = ErrorDetails.builder()
        .field("field")
        .target("target")
        .violationMessage("violation message")
        .build();
    assertThat(body.getDetails()).hasSize(1).containsExactly(errorDetail);
  }

}