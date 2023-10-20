package com.felpeto.purchase.model;

import static java.text.MessageFormat.format;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(level = PRIVATE)
public record Purchase(UUID uuid,
                       Description description,
                       LocalDateTime transactionDate,
                       Money amount) {

  private static final String MANDATORY_FIELD = "[Purchase] | {0} is mandatory";

  public Purchase(final UUID uuid,
      final Description description,
      final LocalDateTime transactionDate,
      final Money amount) {

    this.uuid = uuid;
    this.description = requireNonNull(description, format(MANDATORY_FIELD, Fields.description));
    this.transactionDate = requireNonNull(transactionDate, format(MANDATORY_FIELD, Fields.transactionDate));
    this.amount = requireNonNull(amount, format(MANDATORY_FIELD, Fields.amount));
  }
}
