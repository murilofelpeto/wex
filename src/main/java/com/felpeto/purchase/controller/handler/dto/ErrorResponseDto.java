package com.felpeto.purchase.controller.handler.dto;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDto {

  private String code;
  private String message;
  private Integer status;
  private String parameter;
  private LocalDateTime timestamp;
  private Set<ErrorDetails> details;
}
