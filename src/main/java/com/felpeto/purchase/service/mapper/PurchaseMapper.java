package com.felpeto.purchase.service.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.felpeto.purchase.client.dto.Data;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.felpeto.purchase.repository.PurchaseEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class PurchaseMapper {

  public static Purchase toPurchase(final PurchaseEntity entity) {
    return Purchase.builder()
        .amount(Money.roundUp(entity.amount))
        .description(Description.of(entity.description))
        .transactionDate(entity.transactionDate)
        .uuid(entity.uuid)
        .build();
  }

  public static Purchase toPurchase(final PurchaseEntity entity,
      final Data exchangeRate) {

    return Purchase.withExchangeRate()
        .amount(Money.roundUp(entity.amount))
        .description(Description.of(entity.description))
        .exchangeRate(Money.of(exchangeRate.getExchangeRate()))
        .transactionDate(entity.transactionDate)
        .uuid(entity.uuid)
        .buildWithExchangeRate();
  }
}
