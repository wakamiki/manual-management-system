package com.example.manual.exception;

public class InvalidStateException extends RuntimeException {
//状態不正
  public InvalidStateException(String message) {
    super(message);
  }
}
