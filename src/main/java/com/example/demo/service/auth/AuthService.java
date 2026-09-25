package com.example.demo.service.auth;

import com.example.demo.config.JwtProps;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.model.AppUser;
import com.example.demo.model.RefreshToken;
import com.example.demo.repo.RefreshTokenRepository;
import com.example.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final SecretKey secretKey;
  private final JwtProps jwtProps;
  private final RefreshTokenRepository refreshTokenRepository;

  public AuthResponse login(LoginRequest loginRequest) {
    final var unauthenticatedToken = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
    final var authenticatedToken = authenticationManager.authenticate(unauthenticatedToken);
    final var username = (authenticatedToken.getPrincipal() instanceof AppUser appUser) ?
        appUser.getUsername() : "";
    final List<?> roles = (authenticatedToken.getPrincipal() instanceof AppUser appUser) ?
        appUser.getRoles() : Collections.emptyList();

    final var expirationTimeAccessToken = jwtProps.getExpirationTimeAccessToken();
    final var accessToken = JwtUtils.generateAccessToken(username, roles, secretKey, expirationTimeAccessToken);
    final var expirationTimeRefreshToken = jwtProps.getExpirationTimeRefreshToken();
    final var refreshToken = JwtUtils.generateRefreshToken(username, secretKey, expirationTimeRefreshToken);
    refreshTokenRepository.save(RefreshToken.builder()
        .id(Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8)))
        .token(refreshToken).
        expirationTime(expirationTimeRefreshToken).build());
    return new AuthResponse(accessToken, refreshToken,"Bearer", expirationTimeAccessToken);
  }
}
