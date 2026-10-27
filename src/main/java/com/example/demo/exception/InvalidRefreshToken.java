package com.example.demo.exception;

public class InvalidRefreshToken extends RuntimeException {
  public InvalidRefreshToken(String string) {
    super(string);
  }
}
