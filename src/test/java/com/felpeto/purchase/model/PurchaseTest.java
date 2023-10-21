package com.felpeto.purchase.model;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.github.javafaker.Faker;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PurchaseTest {

  private static final String MANDATORY_FIELD = "[Purchase] | {0} is mandatory";
  private static final Faker faker = new Faker();

  private static Stream<Arguments> invalidParams() {
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDateTime.now();
    final var amount = Money.of(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
    return Stream.of(
        Arguments.of(
            null,
            transactionDate,
            amount,
            format(MANDATORY_FIELD, "description")),
        Arguments.of(
            description,
            null,
            amount,
            format(MANDATORY_FIELD, "transactionDate")),
        Arguments.of(
            description,
            transactionDate,
            null,
            format(MANDATORY_FIELD, "amount")));
  }

  @Test
  void validToString() {
    ToStringVerifier.forClass(Purchase.class)
        .verify();
  }

  @Test
  void validEqualsAndHashCode() {
    EqualsVerifier.forClass(Purchase.class)
        .suppress(Warning.NULL_FIELDS)
        .verify();
  }

  @Test
  void givenAllParametersWhenCreatePurchaseThenReturnValidPurchase() {
    final var uuid = UUID.randomUUID();
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDateTime.now();
    final var amount = Money.of(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));

    final var purchase = new Purchase(uuid, description, transactionDate, amount);

    assertThat(purchase).isNotNull();
    assertThat(purchase.uuid()).isEqualTo(uuid);
    assertThat(purchase.description().getValue()).isEqualTo(description.getValue());
    assertThat(purchase.transactionDate()).isEqualTo(transactionDate);
    assertThat(purchase.amount().getValue()).isEqualTo(amount.getValue());
  }

  @Test
  void givenNullUuidWhenCreatePurchaseThenReturnValidPurchase() {
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDateTime.now();
    final var amount = Money.of(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));

    final var purchase = new Purchase(null, description, transactionDate, amount);

    assertThat(purchase).isNotNull();
    assertThat(purchase.uuid()).isNull();
    assertThat(purchase.description().getValue()).isEqualTo(description.getValue());
    assertThat(purchase.transactionDate()).isEqualTo(transactionDate);
    assertThat(purchase.amount().getValue()).isEqualTo(amount.getValue());
  }

  @ParameterizedTest
  @MethodSource("invalidParams")
  void givenInvalidParametersWhenCreatePurchaseThenThrowException(final Description description,
      final LocalDateTime transactionDate,
      final Money amount,
      final String message) {

    assertThatThrownBy(() -> new Purchase(UUID.randomUUID(), description, transactionDate, amount))
        .isExactlyInstanceOf(NullPointerException.class)
        .hasMessage(message);
  }
}