package com.felpeto.purchase.client.handler.exception;

public final class ClientInternalServerErrorException extends ClientException {

  public ClientInternalServerErrorException(final String target,
      final String message,
      final int status) {

    super(target, message, status);
  }
}
