package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AppUserService;
import com.example.demo.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${app.api.v1}/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AppUserService appUserService;
  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
    appUserService.registerUser(registerRequest);
    final var loginRequest = new LoginRequest(registerRequest.username(), registerRequest.password());
    return authService.login(loginRequest);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
    var authResponse = authService.login(loginRequest);
    return ResponseEntity.ok(authResponse);
  }
}
