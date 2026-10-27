
package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
  }

  public static String generateAccessToken(
      String username,
      Collection<? extends GrantedAuthority> roles,
      SecretKey secretKey,
      long expirationTimeSeconds) {

    if (roles == null || roles.isEmpty()) {
      throw new IllegalArgumentException("Access token roles must not be empty");
    }

    List<String> roleNames = roles.stream()
        .map(authority -> {

          if (authority == null) {
            throw new IllegalArgumentException(
                "Role must not be null");
          }
          return authority.getAuthority();
        }).toList();

    validateRoles(roleNames);
    return generateToken(
        username, secretKey, expirationTimeSeconds,
        ACCESS_TOKEN, roleNames);
  }

  public static String generateRefreshToken(
      String username,
      SecretKey secretKey,
      long expirationTimeSeconds) {

    return generateToken(
        username, secretKey, expirationTimeSeconds,
        REFRESH_TOKEN, null);
  }

  private static String generateToken(
      String username,
      SecretKey secretKey,
      long expirationTimeSeconds,
      String tokenType,
      List<String> roleNames) {

    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException(
          "Username must not be blank");
    }

    Objects.requireNonNull(secretKey, "Secret key must not be null");

    if (expirationTimeSeconds <= 0) {
      throw new IllegalArgumentException(
          "Expiration must be positive");
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
      builder.claim(ROLES_CLAIM, roleNames);
    }

    return builder.signWith(secretKey).compact();
  }

  private static Claims parseToken(
      String token, SecretKey secretKey) {

    if (token == null || token.isBlank()) {
      throw new IllegalArgumentException(
          "JWT must not be blank");
    }

    Objects.requireNonNull(secretKey, "Secret key must not be null");

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public static Claims parseAccessToken(
      String token, SecretKey secretKey) {
    Claims claims = parseToken(token, secretKey);

    if (!ACCESS_TOKEN.equals(
        claims.get(TOKEN_TYPE_CLAIM, String.class))) {
      throw new JwtException("Expected an access token");
    }
    return claims;
  }

  public static Claims parseRefreshToken(
      String token, SecretKey secretKey) {

    Claims claims = parseToken(token, secretKey);

    if (!REFRESH_TOKEN.equals(
        claims.get(TOKEN_TYPE_CLAIM, String.class))) {
      throw new JwtException("Expected a refresh token");
    }

    return claims;
  }

  private static void validateRoles(List<String> roles) {
    if (roles.isEmpty()) {
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

  public static List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
    Objects.requireNonNull(claims, "Claims must not be null");
    Object rolesClaim = claims.get(ROLES_CLAIM);

    if (!(rolesClaim instanceof List<?> roles)) {
      throw new JwtException(
          "Access token must contain a roles list");
    }

    List<String> roleNames = roles.stream()
        .map(role -> {
          if (!(role instanceof String name)) {
            throw new JwtException(
                "Role claim must contain strings");
          }
          return name;
        })
        .toList();

    validateRoles(roleNames);

    return roleNames.stream()
        .map(SimpleGrantedAuthority::new)
        .toList();
  }
}
