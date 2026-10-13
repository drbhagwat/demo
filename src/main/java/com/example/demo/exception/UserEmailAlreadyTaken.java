package com.example.demo.exception;

public class UserEmailAlreadyTaken extends RuntimeException {
  public UserEmailAlreadyTaken(String email) {
    super("Email " + email + " is already taken.");
  }
}
