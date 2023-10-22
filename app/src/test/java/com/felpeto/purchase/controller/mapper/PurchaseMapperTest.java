package com.felpeto.purchase.controller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PurchaseMapperTest {

  private final Faker faker = new Faker();

  @Test
  void givenPurchaseRequestDtoWhenMapThenReturnPurchase() {
    final var request = createPurchaseRequestDto();

    final var purchase = PurchaseMapper.toPurchase(request);

    assertThat(purchase).isNotNull();
    assertThat(purchase.getUuid()).isNull();
    assertThat(purchase.getAmount().getValue()).isEqualTo(request.getAmount().setScale(2, RoundingMode.HALF_UP));
    assertThat(purchase.getDescription().getValue()).isEqualTo(request.getDescription());
    assertThat(purchase.getTransactionDate()).isEqualTo(request.getTransactionDate());
  }

  @Test
  void givenPurchaseWhenMapThenReturnPurchaseResponseDto() {
    final var purchase = createPurchase();

    final var response = PurchaseMapper.toPurchaseResponseDto(purchase);

    assertThat(response).isNotNull();
    assertThat(response.getConvertedMoney()).isNull();
    assertThat(response.getExchangeRate()).isNull();
    assertThat(response.getAmount().setScale(2, RoundingMode.HALF_UP)).isEqualTo(purchase.getAmount().getValue());
    assertThat(response.getDescription()).isEqualTo(purchase.getDescription().getValue());
    assertThat(response.getId()).isEqualTo(purchase.getUuid());
    assertThat(response.getTransactionDate()).isEqualTo(purchase.getTransactionDate());
  }

  @Test
  void givenPurchaseWithExchangeRateWhenMapThenReturnResponseWithConvertedValues() {
    final var uuid = UUID.randomUUID();
    final var purchase = createPurchaseWithExchangeRate(uuid);
    final var convertedMoney = calculateConversionRate(purchase);
    final var response = PurchaseMapper.toPurchaseResponseDto(purchase);

    assertThat(response).isNotNull();
    assertThat(response.getConvertedMoney()).isEqualTo(convertedMoney.getValue());
    assertThat(response.getExchangeRate()).isEqualTo(purchase.getExchangeRate().getValue());
  }

  private Purchase createPurchaseWithExchangeRate(final UUID uuid) {
    final var amount = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100));
    final var exchangeRate = BigDecimal.valueOf(faker.number().randomDouble(4, 1, 100));
    return Purchase.withExchangeRate()
        .amount(Money.roundUp(amount))
        .description(Description.of(faker.harryPotter().quote()))
        .exchangeRate(Money.of(exchangeRate))
        .transactionDate(LocalDate.now())
        .uuid(uuid)
        .buildWithExchangeRate();
  }

  private Money calculateConversionRate(final Purchase purchase) {
    return Money.roundUp(
        purchase.getAmount().getValue().multiply(
            purchase.getExchangeRate().getValue()));
  }

  private Purchase createPurchase() {
    return new Purchase(UUID.randomUUID(),
        Description.of(faker.rickAndMorty().location()),
        LocalDate.now(),
        Money.roundUp(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100))
            .setScale(2, RoundingMode.HALF_UP)));
  }

  private PurchaseRequestDto createPurchaseRequestDto() {
    final var now = LocalDate.now();
    return PurchaseRequestDto.builder()
        .amount(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
        .description(faker.overwatch().location())
        .transactionDate(now)
        .build();
  }
}