package com.felpeto.purchase.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.felpeto.purchase.controller.configuration.BigDecimalSerializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PurchaseResponseDto {

  private UUID id;
  private String description;
  private LocalDate transactionDate;
  private BigDecimal exchangeRate;

  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal amount;

  @JsonSerialize(using = BigDecimalSerializer.class)
  private BigDecimal convertedMoney;
}
