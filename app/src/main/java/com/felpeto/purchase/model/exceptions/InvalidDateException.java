package com.felpeto.purchase.model.exceptions;

public final class InvalidDateException extends UnprocessableEntityException {

  public InvalidDateException(final String message,
      final String parameter,
      final String target,
      final String field,
      final String violationMessage) {

    super(message, parameter, target, field, violationMessage);
  }
}
