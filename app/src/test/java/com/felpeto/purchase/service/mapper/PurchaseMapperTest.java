package com.felpeto.purchase.service.mapper;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;

import com.felpeto.purchase.client.dto.Data;
import com.felpeto.purchase.model.vo.Money;
import com.felpeto.purchase.repository.PurchaseEntity;
import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PurchaseMapperTest {

  private final Faker faker = new Faker();

  @Test
  void givenPurchaseEntityWhenMapThenReturnPurchase() {
    final var entity = new PurchaseEntity();
    entity.id = 1L;
    entity.uuid = UUID.randomUUID();
    entity.description = faker.hitchhikersGuideToTheGalaxy().quote();
    entity.transactionDate = LocalDate.now();
    entity.amount = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)).setScale(2, HALF_UP);

    final var purchase = PurchaseMapper.toPurchase(entity);

    assertThat(purchase).isNotNull();
    assertThat(purchase.getConvertedMoney()).isNull();
    assertThat(purchase.getExchangeRate()).isNull();
    assertThat(purchase.getAmount().getValue()).isEqualTo(entity.amount);
    assertThat(purchase.getDescription().getValue()).isEqualTo(entity.description);
    assertThat(purchase.getTransactionDate()).isEqualTo(entity.transactionDate);
    assertThat(purchase.getUuid()).isEqualTo(entity.uuid);
  }

  @Test
  void givenPurchaseEntityWithDataWhenMapThenReturnPurchase() {
    final var entity = new PurchaseEntity();
    entity.id = 1L;
    entity.uuid = UUID.randomUUID();
    entity.description = faker.hitchhikersGuideToTheGalaxy().quote();
    entity.transactionDate = LocalDate.now();
    entity.amount = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)).setScale(2, HALF_UP);

    final var exchangeRate = Money.of(BigDecimal.valueOf(faker.number().randomDouble(4, 1, 100)));
    final var data = Data.builder()
        .currency("Real")
        .exchangeRate(exchangeRate.getValue())
        .recordDate(LocalDate.now().toString())
        .build();

    final var convertedMoney = Money.roundUp(entity.amount.multiply(exchangeRate.getValue()));

    final var purchase = PurchaseMapper.toPurchase(entity, data);

    assertThat(purchase).isNotNull();
    assertThat(purchase.getConvertedMoney()).isEqualTo(convertedMoney);
    assertThat(purchase.getExchangeRate()).isEqualTo(exchangeRate);
    assertThat(purchase.getAmount().getValue()).isEqualTo(entity.amount);
    assertThat(purchase.getDescription().getValue()).isEqualTo(entity.description);
    assertThat(purchase.getTransactionDate()).isEqualTo(entity.transactionDate);
    assertThat(purchase.getUuid()).isEqualTo(entity.uuid);
  }
}