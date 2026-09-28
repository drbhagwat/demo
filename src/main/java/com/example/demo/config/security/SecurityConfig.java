package com.example.demo.config.security;

import com.example.demo.config.JwtProps;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                          JwtAuthenticationFilter jwtAuthenticationFilter,
                                          AuthenticationEntryPoint authenticationEntryPoint) {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(http -> http
            .requestMatchers("/api/v1/auth/**").permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint));
    return httpSecurity.build();
  }

  @Bean
  AuthenticationManager authenticationManager(@Qualifier("daoAuthenticationProvider") AuthenticationProvider daoAuthenticationProvider, @Qualifier("bearerAuthProvider") BearerAuthProvider bearerAuthenticationProvider) {
    final List<AuthenticationProvider> authenticationProvider = List.of(daoAuthenticationProvider, bearerAuthenticationProvider);
    final var providerManager = new ProviderManager(authenticationProvider);
    return providerManager;
  }

  @Bean("daoAuthenticationProvider")
  AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
    final var authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  SecretKey secretKey(JwtProps jwtProps) {
    return Keys.hmacShaKeyFor(jwtProps.getSecretKey()
        .getBytes(StandardCharsets.UTF_8));
  }
}
