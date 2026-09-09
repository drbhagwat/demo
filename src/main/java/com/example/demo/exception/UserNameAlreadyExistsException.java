package com.example.demo.exception;

public class UserNameAlreadyExistsException extends RuntimeException {
  public UserNameAlreadyExistsException(String userName) {
    super("Username " + userName + " already exists.");
  }
}