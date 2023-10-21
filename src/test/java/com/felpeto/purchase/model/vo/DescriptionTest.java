package com.felpeto.purchase.model.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.felpeto.purchase.model.exceptions.InvalidStringFormatException;
import com.github.javafaker.Faker;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DescriptionTest {

  private static final String MANDATORY_FIELD = "Description is mandatory";
  private static final String FIELD = "Description.value";
  private static final String TARGET = Description.class.getSimpleName();
  private static final String VIOLATION_MESSAGE = "The Description name must not be blank or null";
  private static final Faker faker = new Faker();

  private static Stream<String> invalidParams() {
    return Stream.of(null, "", "  ");
  }

  @Test
  void validToString() {
    ToStringVerifier.forClass(Description.class)
        .verify();
  }

  @Test
  void validEqualsAndHashCode() {
    EqualsVerifier.forClass(Description.class)
        .verify();
  }

  @Test
  void givenValidParametersWhenBuildDescriptionThenReturnValidDescription() {
    final var value = faker.medical().diseaseName();

    final var description = Description.of(value);

    assertThat(description.getValue()).isEqualTo(value);
  }

  @ParameterizedTest
  @MethodSource("invalidParams")
  void givenInvalidParametersWhenBuildDescriptionThenThrowException(final String value) {
    final var exception = catchThrowableOfType(() -> Description.of(value),
        InvalidStringFormatException.class);

    assertThat(exception.getMessage()).isEqualTo(MANDATORY_FIELD);
    assertThat(exception.getParameter()).isEqualTo(FIELD);
    assertThat(exception.getTarget()).isEqualTo(TARGET);
    assertThat(exception.getField()).isEqualTo(FIELD);
    assertThat(exception.getViolationMessage()).isEqualTo(VIOLATION_MESSAGE);
  }
}