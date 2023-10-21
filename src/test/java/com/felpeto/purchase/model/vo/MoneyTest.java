package com.felpeto.purchase.model.vo;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.felpeto.purchase.model.exceptions.InvalidNumberLimitException;
import com.github.javafaker.Faker;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class MoneyTest {

  private static final String MANDATORY_FIELD = "Money is mandatory";
  private static final String INVALID_NUMBER = "value must be greater than or equal to 0: {0}";
  private static final String FIELD = "Money.value";
  private static final String TARGET = Money.class.getSimpleName();
  private static final String VIOLATION_MESSAGE = "When you build a Money, you must provide a number greater or equal than 0";

  private final Faker faker = new Faker();

  @Test
  void validToString() {
    ToStringVerifier.forClass(Money.class)
        .verify();
  }

  @Test
  void validEqualsAndHashCode() {
    EqualsVerifier.forClass(Money.class)
        .suppress(Warning.NULL_FIELDS)
        .verify();
  }

  @Test
  void givenValueOneWhenBuildMoneyThenReturnValidMoney() {
    final var one = BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP);
    final var money = Money.of(one);
    assertThat(money.getValue()).isEqualTo(one);
    assertThat(money.getValue().scale()).isEqualTo(2);
  }

  @Test
  void givenBelowOneWhenBuildMoneyThenReturnValidMoney() {
    final var zero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    final var money = Money.of(zero);

    assertThat(money.getValue()).isEqualTo(zero);
    assertThat(money.getValue().scale()).isEqualTo(2);
  }

  @Test
  void givenNegativeValueWhenBuildMoneyThenThrowException() {
    final var value = BigDecimal.valueOf(faker.number().randomDouble(2, -5, -1));
    final var exception = catchThrowableOfType(() -> Money.of(value),
        InvalidNumberLimitException.class);

    assertThat(exception.getMessage()).isEqualTo(format(INVALID_NUMBER, value));
    assertThat(exception.getParameter()).isEqualTo(FIELD);
    assertThat(exception.getTarget()).isEqualTo(TARGET);
    assertThat(exception.getField()).isEqualTo(FIELD);
    assertThat(exception.getViolationMessage()).isEqualTo(VIOLATION_MESSAGE);
  }

  @Test
  void givenNullValueWhenBuildMoneyThenThrowException() {
    assertThatThrownBy(() -> Money.of(null))
        .isExactlyInstanceOf(NullPointerException.class)
        .hasMessage(MANDATORY_FIELD);
  }

}