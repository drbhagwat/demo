package com.example.demo.exception;

import com.example.demo.dto.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Objects;


@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(UserNameAlreadyExistsException.class)
  public ResponseEntity<?> handleException(UserNameAlreadyExistsException ex) {
    final var status = HttpStatus.CONFLICT.value();
    final var message = ex.getMessage();

    return ResponseEntity
        .status(status)
        .body(generateApiResponse(status, message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleException(Exception ex) {
    final var status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    final var message = ex.getMessage();

    return ResponseEntity
        .status(status)
        .body(generateApiResponse(status, message));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
    final String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse("Validation failed");
    final var status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    return ResponseEntity
        .status(status)
        .body(generateApiResponse(status, message));
  }

  private static ApiResponse generateApiResponse(int status, String message) {
    return new ApiResponse(status, message, Instant.now().toString());
  }
}