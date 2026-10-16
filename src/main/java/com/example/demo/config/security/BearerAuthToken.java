package com.example.demo.config.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class BearerAuthToken extends AbstractAuthenticationToken {
  public static final String BEARER_AUTHENTICATION = "Bearer";
  private final String username;
  private final String credentials;

  private BearerAuthToken(@Nullable Collection<? extends GrantedAuthority> authorities,
                          String username,
                          String credentials,
                          boolean isAuthenticated) {
    super(authorities);
    this.username = username;
    this.credentials = credentials;
    this.setAuthenticated(isAuthenticated);
  }

  public static BearerAuthToken unAuthenticated(String token) {
    return new BearerAuthToken(null, null, token, false);
  }

  public static BearerAuthToken authenticated(String username, Collection<? extends GrantedAuthority> roles) {
    return new BearerAuthToken(roles, username, null,true);
  }

  @Override
  public @Nullable Object getCredentials() {
    return credentials;
  }

  @Override
  public @Nullable Object getPrincipal() {
    return username;
  }
}
