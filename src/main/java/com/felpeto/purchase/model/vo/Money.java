package com.felpeto.purchase.model.vo;

import static java.text.MessageFormat.format;

import com.felpeto.purchase.model.exceptions.InvalidNumberLimitException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class Money {

  private static final String INVALID_NUMBER = "value must be greater than or equal to 0: {0}";
  private static final String FIELD = "Money.value";
  private static final String TARGET = Money.class.getSimpleName();
  private static final String VIOLATION_MESSAGE = "When you build a Money, you must provide a number greater or equal than 1";

  private final BigDecimal value;

  private Money(final BigDecimal value) {
    this.value = value;
  }

  public static Money of(final BigDecimal value) {
    if (value == null) {
      return new Money(null);
    }

    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidNumberLimitException(format(INVALID_NUMBER, value),
          FIELD,
          TARGET,
          FIELD,
          VIOLATION_MESSAGE);
    }
    return new Money(value.setScale(2, RoundingMode.HALF_UP));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Money money = (Money) o;
    return value.compareTo(money.value) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value.stripTrailingZeros());
  }
}
