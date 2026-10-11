package com.example.demo.exception;

import java.util.UUID;

public class AppUserNotFoundException extends RuntimeException {
  public AppUserNotFoundException(UUID id) {
    super("Admin with id " + id.toString() + " is not found");
  }
}
