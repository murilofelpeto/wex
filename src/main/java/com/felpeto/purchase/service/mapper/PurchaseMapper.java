package com.felpeto.purchase.service.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import com.felpeto.purchase.repository.PurchaseEntity;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class PurchaseMapper {

  public static Purchase toPurchase(final PurchaseEntity entity) {
    return new Purchase(
        entity.uuid,
        Description.of(entity.description),
        entity.transactionDate,
        Money.of(entity.amount));
  }
}
