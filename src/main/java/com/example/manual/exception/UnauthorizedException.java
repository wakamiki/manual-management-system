package com.example.manual.exception;

public class UnauthorizedException extends RuntimeException {
//権限なし
  public UnauthorizedException(String message) {
    super(message);
  }
}
