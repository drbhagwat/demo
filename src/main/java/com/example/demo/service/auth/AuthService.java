package com.example.demo.service.auth;

import com.example.demo.config.JwtProps;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.model.AppUser;
import com.example.demo.model.RefreshToken;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.RefreshTokenRepository;
import com.example.demo.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final SecretKey secretKey;
  private final JwtProps jwtProps;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;


  public AuthResponse login(LoginRequest loginRequest) {
    final var unauthenticatedToken = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
    final var authenticatedToken = authenticationManager.authenticate(unauthenticatedToken);

    if (!(authenticatedToken.getPrincipal() instanceof AppUser appUser)) {
      throw new IllegalStateException(
          "Authenticated principal is not an AppUser"
      );
    }
    String username = appUser.getUsername();
    List<String> roles = appUser.getRoles();

    final var expirationTimeAccessToken = jwtProps.getExpirationTimeAccessToken();
    final var accessToken = JwtUtils.generateAccessToken(username, roles, secretKey, expirationTimeAccessToken);
    final var expirationTimeRefreshToken = jwtProps.getExpirationTimeRefreshToken();
    final var refreshToken = JwtUtils.generateRefreshToken(username, secretKey, expirationTimeRefreshToken);
    refreshTokenRepository.save(RefreshToken.builder()
        .id(Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8)))
        .token(refreshToken).
        expirationTime(expirationTimeRefreshToken).build());

    return new AuthResponse(accessToken, refreshToken, "Bearer", expirationTimeAccessToken);
  }

  @Transactional
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null ||
        !authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken) {
      throw new AccessDeniedException("Authentication required");
    }
    String currentUserName = authentication.getName();

    AppUser dbAppUser = appUserRepository.findByUsername(currentUserName)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    String dbPassword = dbAppUser.getPassword();

    if (!passwordEncoder.matches(
        changePasswordRequest.currentPassword(), dbPassword)) {
      throw new BadCredentialsException("Current password is incorrect");
    }
    String newPassword = changePasswordRequest.newPassword();

    if (passwordEncoder.matches(newPassword, dbPassword)) {
      throw new IllegalArgumentException("New password must differ from current password");
    }
    dbAppUser.setPassword(passwordEncoder.encode(newPassword));
  }
}


