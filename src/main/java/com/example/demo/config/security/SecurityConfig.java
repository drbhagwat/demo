package com.example.demo.config.security;

import com.example.demo.config.JwtConfig;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable);
    httpSecurity.authorizeHttpRequests(http -> http.anyRequest().permitAll());
    return httpSecurity.build();
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationProvider daoAuthenticationProvider) {
    final List<AuthenticationProvider> authenticationProvider = List.of(daoAuthenticationProvider);
    final var providerManager = new ProviderManager(authenticationProvider);
    return providerManager;
  }

  @Bean
  AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
    final var authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  SecretKey secretKey(JwtConfig jwtConfig) {
    return Keys.hmacShaKeyFor(jwtConfig.getSecretKey()
        .getBytes(StandardCharsets.UTF_8));
  }
}
