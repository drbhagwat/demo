package com.example.demo.config.security;

import com.example.demo.util.JwtUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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

@Component("bearerAuthProvider")
@RequiredArgsConstructor
public class BearerAuthProvider implements AuthenticationProvider {
  private final SecretKey secretKey;

  @Override
  public @Nullable Authentication authenticate(@Nullable Authentication authentication) throws AuthenticationException {
    if (!(authentication instanceof BearerAuthToken authToken)) {
      throw new BadCredentialsException("Unsupported authentication type");
    }
    final var claims = JwtUtils.parseToken(
        (String) authToken.getCredentials(), secretKey);
    final var username = claims.getSubject();

    if (username == null || username.isBlank()) {
      throw new BadCredentialsException("JWT has no subject");
    }

    List<?> rawRoles = claims.get("roles", List.class);

    if (rawRoles == null || rawRoles.isEmpty()) {
      throw new BadCredentialsException("JWT has no roles");
    }

    if (!rawRoles.stream().allMatch(
        role -> role instanceof String s && !s.isBlank())) {
      throw new BadCredentialsException("JWT contains an invalid role");
    }
    List<String> authorityNames = rawRoles.stream()
        .map(String.class::cast)
        .toList();

    if (!List.of("ROLE_ADMIN", "ROLE_STUDENT").containsAll(authorityNames)) {
      throw new BadCredentialsException("JWT contains an unsupported role");
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
