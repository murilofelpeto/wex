package com.felpeto.purchase.client.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class CurrencyExchangeResponseDto {

  private List<Data> data;
}
