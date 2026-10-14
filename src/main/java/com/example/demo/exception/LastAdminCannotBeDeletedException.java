package com.example.demo.exception;

public class LastAdminCannotBeDeletedException extends RuntimeException {
  public LastAdminCannotBeDeletedException(String userName) {
    super("Username " + userName + " already exists.");
  }
}