package com.example.demo.exception;

public class StudentNotFoundException extends RuntimeException {
  public StudentNotFoundException(Long id) {
    super("Student with id " + id + " is not found");
  }
}
