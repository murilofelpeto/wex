package com.felpeto.purchase.client.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.felpeto.purchase.client.handler.exception.ClientBadRequestException;
import com.felpeto.purchase.client.handler.exception.ClientInternalServerErrorException;
import com.github.javafaker.Faker;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class ClientExceptionHandlerTest {

  private final Faker faker = new Faker();

  private final ClientExceptionHandler handler = new ClientExceptionHandler();

  @Test
  @SuppressWarnings("ThrowableNotThrown")
  void givenBadRequestExceptionWhenToThrowableThenThrowClientBadRequestException() {
    final int status = faker.number().numberBetween(400, 499);
    final var response = Response.status(status).build();

    final var exception = catchThrowableOfType(() -> handler.toThrowable(response),
        ClientBadRequestException.class);

    assertThat(exception.getMessage()).isEqualTo("Unexpected error");
    assertThat(exception.getTarget()).isEqualTo("FiscalData");
    assertThat(exception.getStatus()).isEqualTo(status);
  }
  @Test
  @SuppressWarnings("ThrowableNotThrown")
  void givenError400WhenToThrowableThenThrowClientBadRequestException() {
    final var response = Response.status(400).build();

    final var exception = catchThrowableOfType(() -> handler.toThrowable(response),
        ClientBadRequestException.class);

    assertThat(exception.getMessage()).isEqualTo("Unexpected error");
    assertThat(exception.getTarget()).isEqualTo("FiscalData");
    assertThat(exception.getStatus()).isEqualTo(400);
  }
  @Test
  @SuppressWarnings("ThrowableNotThrown")
  void givenError49900WhenToThrowableThenThrowClientBadRequestException() {
    final var response = Response.status(499).build();

    final var exception = catchThrowableOfType(() -> handler.toThrowable(response),
        ClientBadRequestException.class);

    assertThat(exception.getMessage()).isEqualTo("Unexpected error");
    assertThat(exception.getTarget()).isEqualTo("FiscalData");
    assertThat(exception.getStatus()).isEqualTo(499);
  }

  @Test
  @SuppressWarnings("ThrowableNotThrown")
  void givenInternalServerExceptionWhenToThrowableThenThrowClientInternalServerException() {
    final int status = faker.number().numberBetween(500, 599);
    final var response = Response.status(status).build();

    final var exception = catchThrowableOfType(() -> handler.toThrowable(response),
        ClientInternalServerErrorException.class);

    assertThat(exception.getMessage()).isEqualTo("Something went wrong, try again later");
    assertThat(exception.getTarget()).isEqualTo("FiscalData");
    assertThat(exception.getStatus()).isEqualTo(status);
  }
  @Test
  @SuppressWarnings("ThrowableNotThrown")
  void givenError500WhenToThrowableThenThrowClientInternalServerException() {
    final var response = Response.status(500).build();

    final var exception = catchThrowableOfType(() -> handler.toThrowable(response),
        ClientInternalServerErrorException.class);

    assertThat(exception.getMessage()).isEqualTo("Something went wrong, try again later");
    assertThat(exception.getTarget()).isEqualTo("FiscalData");
    assertThat(exception.getStatus()).isEqualTo(500);
  }
  @Test
  @SuppressWarnings("ThrowableNotThrown")
  void givenError599WhenToThrowableThenThrowClientInternalServerException() {
    final var response = Response.status(599).build();

    final var exception = catchThrowableOfType(() -> handler.toThrowable(response),
        ClientInternalServerErrorException.class);

    assertThat(exception.getMessage()).isEqualTo("Something went wrong, try again later");
    assertThat(exception.getTarget()).isEqualTo("FiscalData");
    assertThat(exception.getStatus()).isEqualTo(599);
  }

  @Test
  void givenOtherStatusWhenToThrowableThenReturnNull() {
    final int status = faker.number().numberBetween(100, 399);
    final var response = Response.status(status).build();

    final var exception = handler.toThrowable(response);

    assertThat(exception).isNull();
  }

}