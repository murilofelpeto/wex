package com.felpeto.purchase.model;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.github.javafaker.Faker;
import com.jparams.verifier.tostring.ToStringVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PurchaseTest {

  private static final String MANDATORY_FIELD = "[Purchase] | {0} is mandatory";
  private static final Faker faker = new Faker();

  private static Stream<Arguments> invalidParams() {
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDate.now();
    final var amount = Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
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

  private static Stream<Arguments> invalidParamsWithExchangeRate() {
    final var amount = Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
    final var description = Description.of(faker.harryPotter().quote());
    final var exchangeRate = Money.of(BigDecimal.valueOf(faker.number().randomDouble(4, 1, 100)));
    final var transactionDate = LocalDate.now();
    final var uuid = UUID.randomUUID();
    return Stream.of(
        Arguments.of(
            null,
            description,
            exchangeRate,
            transactionDate,
            uuid,
            format(MANDATORY_FIELD, "amount")),
        Arguments.of(
            amount,
            null,
            exchangeRate,
            transactionDate,
            uuid,
            format(MANDATORY_FIELD, "description")),
        Arguments.of(
            amount,
            description,
            null,
            transactionDate,
            uuid,
            format(MANDATORY_FIELD, "exchangeRate")),
        Arguments.of(
            amount,
            description,
            exchangeRate,
            null,
            uuid,
            format(MANDATORY_FIELD, "transactionDate")),
        Arguments.of(
            amount,
            description,
            exchangeRate,
            transactionDate,
            null,
            format(MANDATORY_FIELD, "uuid")));
  }

  @Test
  void validToString() {
    ToStringVerifier.forClass(Purchase.class)
        .verify();
  }

  @Test
  void validEqualsAndHashCode() {
    EqualsVerifier.forClass(Purchase.class)
        .withOnlyTheseFields("uuid")
        .verify();
  }

  @Test
  void givenAllParametersWhenCreatePurchaseThenReturnValidPurchase() {
    final var uuid = UUID.randomUUID();
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDate.now();
    final var amount = Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));

    final var purchase = Purchase.builder()
        .amount(amount)
        .description(description)
        .transactionDate(transactionDate)
        .uuid(uuid)
        .build();

    assertThat(purchase).isNotNull();
    assertThat(purchase.getUuid()).isEqualTo(uuid);
    assertThat(purchase.getDescription().getValue()).isEqualTo(description.getValue());
    assertThat(purchase.getTransactionDate()).isEqualTo(transactionDate);
    assertThat(purchase.getAmount().getValue()).isEqualTo(amount.getValue());
  }

  @Test
  void givenNullUuidWhenCreatePurchaseThenReturnValidPurchase() {
    final var description = Description.of(faker.harryPotter().quote());
    final var transactionDate = LocalDate.now();
    final var amount = Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));

    final var purchase = Purchase.builder()
        .amount(amount)
        .description(description)
        .transactionDate(transactionDate)
        .build();

    assertThat(purchase).isNotNull();
    assertThat(purchase.getUuid()).isNull();
    assertThat(purchase.getDescription().getValue()).isEqualTo(description.getValue());
    assertThat(purchase.getTransactionDate()).isEqualTo(transactionDate);
    assertThat(purchase.getAmount().getValue()).isEqualTo(amount.getValue());
  }

  @Test
  void givenValidExchangeRateWhenCreatePurchaseThenReturnValidPurchase() {
    final var amount = Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)));
    final var description = Description.of(faker.harryPotter().quote());
    final var exchangeRate = Money.of(BigDecimal.valueOf(faker.number().randomDouble(4, 1, 100)));
    final var transactionDate = LocalDate.now();
    final var uuid = UUID.randomUUID();
    final var conversionRate = calculateConversionRate(amount, exchangeRate);
    final var purchase = Purchase.withExchangeRate()
        .amount(amount)
        .description(description)
        .exchangeRate(exchangeRate)
        .transactionDate(transactionDate)
        .uuid(uuid)
        .buildWithExchangeRate();

    assertThat(purchase).isNotNull();
    assertThat(purchase.getUuid()).isEqualTo(uuid);
    assertThat(purchase.getDescription().getValue()).isEqualTo(description.getValue());
    assertThat(purchase.getTransactionDate()).isEqualTo(transactionDate);
    assertThat(purchase.getAmount().getValue()).isEqualTo(amount.getValue());
    assertThat(purchase.getExchangeRate().getValue()).isEqualTo(exchangeRate.getValue());
    assertThat(purchase.getConvertedMoney()).isEqualTo(conversionRate);
  }

  @ParameterizedTest
  @MethodSource("invalidParamsWithExchangeRate")
  void givenInvalidParametersForExchangeRateWhenCreatePurchaseThenThrowException(
      final Money amount,
      final Description description,
      final Money exchangeRate,
      final LocalDate transactionDate,
      final UUID uuid,
      final String message) {

    final var purchaseBuilder = Purchase.withExchangeRate()
        .amount(amount)
        .description(description)
        .exchangeRate(exchangeRate)
        .transactionDate(transactionDate)
        .uuid(uuid);

    assertThatThrownBy(purchaseBuilder::buildWithExchangeRate)
        .isExactlyInstanceOf(NullPointerException.class)
        .hasMessage(message);
  }

  @ParameterizedTest
  @MethodSource("invalidParams")
  void givenInvalidParametersWhenCreatePurchaseThenThrowException(final Description description,
      final LocalDate transactionDate,
      final Money amount,
      final String message) {

    final var purchaseBuilder = Purchase.builder()
        .amount(amount)
        .description(description)
        .transactionDate(transactionDate);

    assertThatThrownBy(purchaseBuilder::build)
        .isExactlyInstanceOf(NullPointerException.class)
        .hasMessage(message);
  }

  private Money calculateConversionRate(final Money amount, final Money exchangeRate) {
    return Money.roundUp(
        amount.getValue().multiply(
            exchangeRate.getValue()));
  }
}