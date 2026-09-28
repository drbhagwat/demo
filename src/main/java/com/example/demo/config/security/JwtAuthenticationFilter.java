package com.example.demo.config.security;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Authenticator;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final AuthenticationManager authenticationManager;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    final var header = request.getHeader(HttpHeaders.AUTHORIZATION);
    final var token = extractToken(header);

    if (token.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }
    final var unAuthenticatedToken = BearerAuthToken.unAuthenticated(token.get());

    try {
      final var authenticatedToken = authenticationManager.authenticate(unAuthenticatedToken);
      SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
    } catch (AuthenticationException e) {
      SecurityContextHolder.clearContext();
      return;
    }
    filterChain.doFilter(request, response);
  }

  private Optional<String> extractToken(final String authorizationHeder) {

    if (StringUtils.isEmpty(authorizationHeder)) {
      return Optional.empty();
    }
    return Optional.of(authorizationHeder.substring(7));
  }
}
