package com.example.demo.exception;

public class StudentEmailAlreadyTaken extends RuntimeException {
  public StudentEmailAlreadyTaken(String email) {
    super("Email " + email + " is already taken.");
  }
}
