package com.felpeto.purchase.client.handler.exception;

import lombok.Getter;

@Getter
public sealed class ClientException extends RuntimeException permits ClientBadRequestException,
    ClientInternalServerErrorException {

  private final String target;
  private final int status;

  public ClientException(final String target,
      final String message,
      final int status) {

    super(message);
    this.target = target;
    this.status = status;
  }
}
