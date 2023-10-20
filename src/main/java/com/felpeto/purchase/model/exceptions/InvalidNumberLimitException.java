package com.felpeto.purchase.model.exceptions;

public final class InvalidNumberLimitException extends UnprocessableEntityException {

  public InvalidNumberLimitException(final String message,
      final String parameter,
      final String target,
      final String field,
      final String violationMessage) {

    super(message, parameter, target, field, violationMessage);
  }
}
