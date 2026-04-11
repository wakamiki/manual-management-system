package com.example.manual.exception;

public class NotFoundException extends RuntimeException {
//見つからない  
public NotFoundException(String message) {
    super(message);
  }
}

