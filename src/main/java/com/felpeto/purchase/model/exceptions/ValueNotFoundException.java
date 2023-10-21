package com.felpeto.purchase.model.exceptions;

import lombok.Getter;

@Getter
public final class ValueNotFoundException extends RuntimeException {

  private final String parameter;
  private final String target;
  private final String field;
  private final String violationMessage;

  public ValueNotFoundException(final String message,
      final String parameter,
      final String target,
      final String field,
      final String violationMessage) {

    super(message);
    this.parameter = parameter;
    this.target = target;
    this.field = field;
    this.violationMessage = violationMessage;
  }
}
