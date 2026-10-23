package com.example.demo.config.security;

import com.example.demo.util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("bearerAuthProvider")
@RequiredArgsConstructor
public class BearerAuthProvider implements AuthenticationProvider {
  private static final Set<String> ALLOWED_ROLES =
      Set.of("ROLE_ADMIN", "ROLE_STUDENT");
  private final SecretKey secretKey;

  @Override
  public @Nullable Authentication authenticate(@Nullable Authentication authentication) throws AuthenticationException {
    if (!(authentication instanceof BearerAuthToken authToken)) {
      return null;
    }
    Object credentials = authToken.getCredentials();

    if (!(credentials instanceof String token) || token.isBlank()) {
      throw new BadCredentialsException("Missing bearer token");
    }
    Claims claims;

    try {
      claims = JwtUtils.parseAccessToken(token, secretKey);
    } catch (JwtException | IllegalArgumentException e) {
      throw new BadCredentialsException("Invalid JWT", e);
    }

    // Verify this is an access token
    if (!JwtUtils.ACCESS_TOKEN.equals(
        claims.get(JwtUtils.TOKEN_TYPE_CLAIM, String.class))) {
      throw new BadCredentialsException("An access token is required");
    }

    final var username = claims.getSubject();

    if (username == null || username.isBlank()) {
      throw new BadCredentialsException("JWT has no subject");
    }
    Object rolesClaim = claims.get("roles");

    if (!(rolesClaim instanceof List<?> rawRoles)
        || rawRoles.isEmpty()) {
      throw new BadCredentialsException("JWT has no valid roles");
    }

    if (!rawRoles.stream().allMatch(
        role -> role instanceof String s && !s.isBlank())) {
      throw new BadCredentialsException("JWT contains an invalid role");
    }
    List<String> authorityNames = rawRoles.stream()
        .map(String.class::cast)
        .toList();

    if (!ALLOWED_ROLES.containsAll(authorityNames)) {
      throw new BadCredentialsException(
          "JWT contains an unsupported role");
    }

    if (new HashSet<>(authorityNames).size() != authorityNames.size()) {
      throw new BadCredentialsException("JWT contains duplicate roles");
    }
    List<GrantedAuthority> authorities = authorityNames.stream()
        .map(SimpleGrantedAuthority::new)
        .map(authority -> (GrantedAuthority) authority)
        .toList();
    return BearerAuthToken.authenticated(username, authorities);
  }

  @Override
  public boolean supports(@NonNull Class<?> authentication) {
    return BearerAuthToken.class.isAssignableFrom(authentication);
  }
}
