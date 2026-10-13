package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "${app.api.v1}/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.login(loginRequest));
  }

  @PatchMapping("/change-password")
  public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
    authService.changePassword(changePasswordRequest);
    return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
  }
}
