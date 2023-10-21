package com.felpeto.purchase.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenApiControllerTest {

  @InjectMocks
  private OpenApiController controller = new OpenApiController();

  @Test
  @DisplayName("Should not be null")
  void whenCallingShouldNotBeNull() {
    assertThat(controller.openApi())
        .isNotNull();
  }

  @Test
  void shouldOpenApiResponseBeEqualsAsTheAssert() {
    final var openApiResponse = new BufferedReader(
        new InputStreamReader(controller.openApi(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    final var openApiAssert = new BufferedReader(
        new InputStreamReader(
            Objects.requireNonNull(getClass().getResourceAsStream("/openapi.json")),
            StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    assertThat(openApiAssert)
        .isNotNull();

    assertThat(openApiResponse)
        .isNotNull()
        .isEqualTo(openApiAssert);
  }

}