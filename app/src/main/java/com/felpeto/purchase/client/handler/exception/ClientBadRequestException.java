package com.felpeto.purchase.client.handler.exception;

public final class ClientBadRequestException extends ClientException {

  public ClientBadRequestException(final String target,
      final String message,
      final int status) {

    super(target, message, status);
  }
}
