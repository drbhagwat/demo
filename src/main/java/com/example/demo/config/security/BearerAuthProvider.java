package com.example.demo.config.security;

import com.example.demo.util.JwtUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component("bearerAuthProvider")
@RequiredArgsConstructor
public class BearerAuthProvider implements AuthenticationProvider {
  private final SecretKey secretKey;

  @Override
  public @Nullable Authentication authenticate(@Nullable Authentication authentication) throws AuthenticationException {
    final var claims = JwtUtils.parseToken((authentication instanceof BearerAuthToken authToken) ? (String) authToken.getCredentials(): "", secretKey);
    final var username = claims.getSubject();
    List<String> authorityNames = claims.get("authorities", List.class);
    List<GrantedAuthority> authorities = authorityNames.stream()
        .map(name -> (GrantedAuthority) new SimpleGrantedAuthority(name))
        .toList();
    return BearerAuthToken.authenticated(username, authorities);
  }

  @Override
  public boolean supports(@NonNull Class<?> authentication) {
    return BearerAuthToken.class.isAssignableFrom(authentication);
  }
}
