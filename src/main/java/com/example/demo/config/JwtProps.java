package com.example.demo.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@RequiredArgsConstructor
public class JwtProps {
  private String secretKey;
  private long expirationTimeAccessToken;
  private long expirationTimeRefreshToken;
}
