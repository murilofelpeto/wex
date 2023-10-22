package com.felpeto.purchase.controller.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class PurchaseRequestDto {

  @Schema(name = "description", description = "Description", requiredMode = REQUIRED)
  @NotBlank(message = "Description cannot be blank")
  @Size(max = 50, message = "Description cannot be longer than 50 characters")
  private String description;

  @Schema(name = "transactionDate", description = "Transaction date", requiredMode = REQUIRED)
  @NotNull(message = "Transaction date cannot be null")
  @PastOrPresent(message = "Transaction date cannot be in the future")
  private LocalDate transactionDate;

  @Schema(name = "amount", description = "Amount", requiredMode = REQUIRED)
  @Digits(fraction = 2, integer = Integer.MAX_VALUE, message = "Amount must be a number")
  @NotNull(message = "Amount cannot be null")
  @Positive(message = "Amount must be positive")
  private BigDecimal amount;
}

