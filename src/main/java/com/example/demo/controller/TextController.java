package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${app.api.v1}/texts")
public class TextController {
  @GetMapping
  public ResponseEntity<?> getSecretText(@AuthenticationPrincipal String username){
    final var response = "Currently logged in as %s".formatted(username);
    return ResponseEntity.ok(response);
  }
}
