package com.felpeto.purchase.controller.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.controller.dto.response.PurchaseResponseDto;
import com.felpeto.purchase.model.Purchase;
import com.felpeto.purchase.model.vo.Description;
import com.felpeto.purchase.model.vo.Money;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class PurchaseMapper {

  public static Purchase toPurchase(final PurchaseRequestDto requestDto) {
    return new Purchase(
        null,
        Description.of(requestDto.getDescription()),
        requestDto.getTransactionDate(),
        Money.roundUp(requestDto.getAmount()));
  }

  public static PurchaseResponseDto toPurchaseResponseDto(final Purchase purchase) {
    return PurchaseResponseDto.builder()
        .amount(purchase.getAmount().getValue())
        .convertedMoney(getValueOrNull(purchase::getConvertedMoney, Money::getValue))
        .description(purchase.getDescription().getValue())
        .exchangeRate(getValueOrNull(purchase::getExchangeRate, Money::getValue))
        .id(purchase.getUuid())
        .transactionDate(purchase.getTransactionDate())
        .build();
  }

  private static <T, U> U getValueOrNull(Supplier<T> supplier, Function<T, U> valueExtractor) {
    return Optional.ofNullable(supplier.get())
        .map(valueExtractor)
        .orElse(null);
  }
}
