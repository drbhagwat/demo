package com.example.demo.util;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JwtUtils {
  static String generateAccessToken(String username,
                                    List<?> roles,
                                    SecretKey secretKey,
                                    long expirationTimeAccessToken) {
    return generateToken(username, roles, secretKey, expirationTimeAccessToken);
  }


  private static String generateToken(String username,
                                            List<?> roles,
                                            SecretKey secretKey,
                                            long expirationTimeAccessToken) {
    final var builder = Jwts.builder();
    final var now = Instant.now();
    final var expiration = now.plusSeconds(expirationTimeAccessToken);

    final var claims = Jwts
        .claims().subject(username)
        .id(UUID.randomUUID().toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiration))
        .add("role", roles)
        .build();


    return builder.claims(claims).signWith(secretKey).compact();
  }
}
