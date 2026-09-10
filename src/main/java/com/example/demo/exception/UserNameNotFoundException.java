package com.example.demo.exception;

public class UserNameNotFoundException extends RuntimeException {
  public UserNameNotFoundException(Long id) {
    super("Student with id " + id + " is not found");
  }
}
