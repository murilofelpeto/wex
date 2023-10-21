package com.felpeto.purchase.controller.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.controller.dto.response.PurchaseResponseDto;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class PurchaseMapper {

  public static Purchase toPurchase(final PurchaseRequestDto requestDto) {
    return new Purchase(
        null,
        Description.of(requestDto.getDescription()),
        requestDto.getTransactionDate(),
        Money.of(requestDto.getAmount()));
  }

  public static PurchaseResponseDto toPurchaseResponseDto(final Purchase purchase) {
    return PurchaseResponseDto.builder()
        .amount(purchase.amount().getValue())
        .description(purchase.description().getValue())
        .id(purchase.uuid())
        .transactionDate(purchase.transactionDate())
        .build();
  }
}
