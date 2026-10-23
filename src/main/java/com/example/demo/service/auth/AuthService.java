package com.example.demo.service.auth;

import com.example.demo.config.JwtProps;
import com.example.demo.config.security.BearerAuthToken;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.TokenRefreshRequest;
import com.example.demo.exception.InvalidRefreshToken;
import com.example.demo.model.AppUser;
import com.example.demo.model.RefreshToken;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.RefreshTokenRepository;
import com.example.demo.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
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
    return getAuthResponse(authenticatedToken);
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
        .orElseThrow(() -> new UsernameNotFoundException("User " + currentUserName + " not found"));
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

  public AuthResponse refreshToken(TokenRefreshRequest request) {
    // Check refresh token belongs to same user as access token or not.
    final var claims = JwtUtils.parseAccessToken(request.accessToken(), secretKey);
    final var username = claims.get(Claims.SUBJECT, String.class);

    final var refreshTokenUsername = JwtUtils.parseRefreshToken(request.refreshToken(), secretKey)
        .get(Claims.SUBJECT, String.class);

    if (!username.equals(refreshTokenUsername)) {
      refreshTokenRepository.deleteById(username);
      throw new InvalidRefreshToken("Token identity mismatch.");
    }
    // Check whether refresh token is same or not.
    final var existingToken = refreshTokenRepository.findById(Base64.getEncoder()
            .encodeToString(username.getBytes(
                StandardCharsets.UTF_8)))
        .orElseThrow(() -> new InvalidRefreshToken("No token found for user."));

    if (!request.refreshToken().equals(existingToken.getToken())) {
      refreshTokenRepository.delete(existingToken);
      throw new InvalidRefreshToken("Token mismatch. Potential reuse detected.");
    }
    // Remove the existing refresh token
    refreshTokenRepository.delete(existingToken);
    return getAuthResponse(BearerAuthToken.authenticated(username, JwtUtils.extractAuthorities(claims)));
  }

  private AuthResponse getAuthResponse(Authentication authenticatedToken) {
    String username = getUsername(authenticatedToken);

    if (username == null || username.isBlank()) {
      throw new BadCredentialsException("Authenticated username is missing");
    }

    // Extract user roles from authenticated token
    final Collection<? extends GrantedAuthority> roles = getAuthorities(authenticatedToken);
    final var expirationTimeAccessToken = jwtProps.getExpirationTimeAccessToken();
    final var accessToken = JwtUtils.generateAccessToken(username, roles, secretKey, expirationTimeAccessToken);
    final var expirationTimeRefreshToken = jwtProps.getExpirationTimeRefreshToken();
    final var refreshToken = JwtUtils.generateRefreshToken(username, secretKey, expirationTimeRefreshToken);
    // Save refresh token --> Cache
    refreshTokenRepository.save(RefreshToken.builder()
        .id(Base64.getEncoder()
            .encodeToString(username.getBytes(StandardCharsets.UTF_8)))
        .token(refreshToken)
        .expirationTime(expirationTimeRefreshToken)
        .build());
    return new AuthResponse(accessToken, refreshToken, "Bearer", expirationTimeAccessToken);
  }

  private static Collection<? extends GrantedAuthority> getAuthorities(
      Authentication authentication) {
    Object principal = authentication.getPrincipal();

    if (principal instanceof AppUser appUser) {
      return appUser.getAuthorities();
    }

    if (principal instanceof UserDetails userDetails) {
      return userDetails.getAuthorities();
    }
    return authentication.getAuthorities();
  }

  private static String getUsername(Authentication authentication) {
    Object principal = authentication.getPrincipal();

    if (principal instanceof AppUser appUser) {
      return appUser.getUsername();
    }

    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }

    if (principal instanceof String username
        && !username.equals("anonymousUser")) {
      return username;
    }

    throw new BadCredentialsException(
        "Unsupported authentication principal"
    );
  }
}


