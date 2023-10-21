package com.felpeto.purchase.controller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PurchaseMapperTest {

  private final Faker faker = new Faker();

  @Test
  void givenPurchaseRequestDtoWhenMapThenReturnPurchase() {
    final var request = createPurchaseRequestDto();

    final var purchase = PurchaseMapper.toPurchase(request);

    assertThat(purchase).isNotNull();
    assertThat(purchase.uuid()).isNull();
    assertThat(purchase.amount().getValue()).isEqualTo(request.getAmount());
    assertThat(purchase.description().getValue()).isEqualTo(request.getDescription());
    assertThat(purchase.transactionDate()).isEqualTo(request.getTransactionDate());
  }

  @Test
  void givenPurchaseWhenMapThenReturnPurchaseResponseDto() {
    final var purchase = createPurchase();

    final var response = PurchaseMapper.toPurchaseResponseDto(purchase);

    assertThat(response).isNotNull();
    assertThat(response.getAmount()).isEqualTo(purchase.amount().getValue());
    assertThat(response.getDescription()).isEqualTo(purchase.description().getValue());
    assertThat(response.getId()).isEqualTo(purchase.uuid());
    assertThat(response.getTransactionDate()).isEqualTo(purchase.transactionDate());
  }

  private Purchase createPurchase() {
    return new Purchase(UUID.randomUUID(),
        Description.of(faker.rickAndMorty().location()),
        LocalDateTime.now(),
        Money.of(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100))
            .setScale(2, RoundingMode.HALF_UP)));
  }

  private PurchaseRequestDto createPurchaseRequestDto() {
    final var now = LocalDateTime.now();
    return PurchaseRequestDto.builder()
        .amount(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
        .description(faker.overwatch().location())
        .transactionDate(now)
        .build();
  }
}