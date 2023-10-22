package com.felpeto.purchase.model;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Getter
@ToString
@FieldNameConstants(level = PRIVATE)
public final class Purchase {

  private static final String MANDATORY_FIELD = "[Purchase] | {0} is mandatory";
  private final UUID uuid;
  private final Description description;
  private final LocalDate transactionDate;
  private final Money amount;

  private final Money exchangeRate;
  private final Money convertedMoney;


  @Builder
  public Purchase(final UUID uuid,
      final Description description,
      final LocalDate transactionDate,
      final Money amount) {

    this.uuid = uuid;
    this.description = requireNonNull(description, format(MANDATORY_FIELD, Fields.description));
    this.amount = requireNonNull(amount, format(MANDATORY_FIELD, Fields.amount));
    this.transactionDate = requireNonNull(transactionDate,
        format(MANDATORY_FIELD, Fields.transactionDate));
    this.exchangeRate = null;
    this.convertedMoney = null;
  }

  @Builder(builderMethodName = "withExchangeRate", buildMethodName = "buildWithExchangeRate")
  public Purchase(final UUID uuid,
      final Description description,
      final LocalDate transactionDate,
      final Money amount,
      final Money exchangeRate) {

    this.uuid = requireNonNull(uuid, format(MANDATORY_FIELD, Fields.uuid));
    this.description = requireNonNull(description, format(MANDATORY_FIELD, Fields.description));
    this.amount = requireNonNull(amount, format(MANDATORY_FIELD, Fields.amount));
    this.exchangeRate = requireNonNull(exchangeRate, format(MANDATORY_FIELD, Fields.exchangeRate));
    this.transactionDate = requireNonNull(transactionDate,
        format(MANDATORY_FIELD, Fields.transactionDate));

    this.convertedMoney = Money.roundUp(amount.getValue().multiply(exchangeRate.getValue()));

  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Purchase purchase = (Purchase) o;
    return Objects.equals(uuid, purchase.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}
