package com.example.demo.exception;

import java.util.UUID;

public class StudentNotFoundException extends RuntimeException {
  public StudentNotFoundException(UUID id) {
    super("Student with id " + id.toString() + " is not found");
  }
}
