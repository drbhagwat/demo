package com.example.demo.service.auth;

import com.example.demo.config.JwtConfig;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.model.AppUser;
import com.example.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final SecretKey secretKey;
  private final JwtConfig jwtConfig;

  public AuthResponse login(LoginRequest loginRequest) {
    final var unauthenticatedToken = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
    final var authenticatedToken = authenticationManager.authenticate(unauthenticatedToken);
    final var username = (authenticatedToken.getPrincipal() instanceof AppUser appUser) ?
        appUser.getUsername() : "";
    final List<?> roles = (authenticatedToken.getPrincipal() instanceof AppUser appUser) ?
        appUser.getRoles() : Collections.emptyList();

    final var accessToken = JwtUtils.generateAccessToken(username, roles, secretKey, jwtConfig.getExpirationTimeAccessToken());
    return new AuthResponse(accessToken, null,  "Bearer", jwtConfig.getExpirationTimeAccessToken());
  }
}
