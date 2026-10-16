package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class JwtUtils {
  public static final String TOKEN_TYPE_CLAIM = "token_type";
  public static final String ACCESS_TOKEN = "access";
  public static final String REFRESH_TOKEN = "refresh";
  public static final String ROLES_CLAIM = "roles";

  private static final Set<String> ALLOWED_ROLES =
      Set.of("ROLE_ADMIN", "ROLE_STUDENT");

  private JwtUtils() {
    // Utility class; prevent instantiation.
  }

  public static String generateAccessToken(
      String username,
      List<String> roles,
      SecretKey secretKey,
      long expirationTimeSeconds) {

    if (roles == null || roles.isEmpty()) {
      throw new IllegalArgumentException(
          "Access token roles must not be empty");
    }

    return generateToken(
        username,
        roles,
        secretKey,
        expirationTimeSeconds,
        ACCESS_TOKEN);
  }

  public static String generateRefreshToken(
      String username,
      SecretKey secretKey,
      long expirationTimeSeconds) {

    return generateToken(
        username,
        null,
        secretKey,
        expirationTimeSeconds,
        REFRESH_TOKEN);
  }

  private static String generateToken(
      String username,
      List<String> roles,
      SecretKey secretKey,
      long expirationTimeSeconds,
      String tokenType) {

    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException(
          "Username must not be blank");
    }

    if (secretKey == null) {
      throw new IllegalArgumentException(
          "Secret key must not be null");
    }

    if (expirationTimeSeconds <= 0) {
      throw new IllegalArgumentException(
          "Expiration must be positive");
    }

    if (!ACCESS_TOKEN.equals(tokenType)
        && !REFRESH_TOKEN.equals(tokenType)) {
      throw new IllegalArgumentException(
          "Invalid token type");
    }

    if (ACCESS_TOKEN.equals(tokenType)) {
      if (roles == null || roles.isEmpty()) {
        throw new IllegalArgumentException(
            "Access token roles must not be empty");
      }

      if (roles.stream().anyMatch(
          role -> role == null || role.isBlank())) {
        throw new IllegalArgumentException(
            "Roles must not contain null or blank values");
      }

      if (!ALLOWED_ROLES.containsAll(roles)) {
        throw new IllegalArgumentException(
            "Roles contain an unsupported value");
      }

      if (Set.copyOf(roles).size() != roles.size()) {
        throw new IllegalArgumentException(
            "Roles must not contain duplicates");
      }
    }

    Instant now = Instant.now();
    Instant expiration = now.plusSeconds(expirationTimeSeconds);

    var builder = Jwts.builder()
        .subject(username)
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .claim(TOKEN_TYPE_CLAIM, tokenType);

    if (ACCESS_TOKEN.equals(tokenType)) {
      builder.claim(ROLES_CLAIM, roles);
    }

    return builder.signWith(secretKey).compact();
  }

  public static Claims parseToken(
      String token,
      SecretKey secretKey) {

    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException(
          "JWT must not be blank");
    }

    if (secretKey == null) {
      throw new IllegalArgumentException(
          "Secret key must not be null");
    }

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}