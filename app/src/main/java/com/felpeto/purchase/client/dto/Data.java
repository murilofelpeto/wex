package com.felpeto.purchase.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Data {

  @JsonProperty("record_date")
  private String recordDate;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("exchange_rate")
  private BigDecimal exchangeRate;
}
