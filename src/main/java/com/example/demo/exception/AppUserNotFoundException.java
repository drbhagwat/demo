package com.example.demo.exception;

import java.util.UUID;

public class AppUserNotFoundException extends RuntimeException {
  public AppUserNotFoundException(UUID id) {
    super("User with id " + id.toString() + " is not found");
  }
}
