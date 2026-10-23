package com.example.demo.exception;

public class InvalidRefreshToken extends RuntimeException {
  public InvalidRefreshToken(String email) {
    super("Email " + email + " is already taken.");
  }
}
