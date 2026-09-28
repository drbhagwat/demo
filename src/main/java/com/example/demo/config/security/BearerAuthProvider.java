package com.example.demo.config.security;

import com.example.demo.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component("bearerAuthProvider")
@RequiredArgsConstructor
public class BearerAuthProvider implements AuthenticationProvider {
  private final SecretKey secretKey;

  @Override
  public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final var claims = JwtUtils.parseToken((authentication instanceof BearerAuthToken authToken) ? (String) authToken.getCredentials(): "", secretKey);
    final var username = claims.getSubject();
    final List<? extends GrantedAuthority> authorities = claims.get("authorities", List.class);
    return BearerAuthToken.authenticated(username, authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(BearerAuthToken.class);
  }
}
