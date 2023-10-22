package com.felpeto.purchase.controller.handler.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetails {

  private String target;
  private String field;
  private String violationMessage;
}