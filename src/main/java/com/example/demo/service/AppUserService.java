package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.exception.UserNameAlreadyExistsException;
import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;

  public ResponseEntity<?> registerUser(RegisterRequest registerRequest) {
    boolean exists = appUserRepository.existsByUsername(registerRequest.username());

    if (exists) {
      throw new UserNameAlreadyExistsException(registerRequest.username());
    }

    var newAppUser = AppUser.builder().fullName(registerRequest.fullName())
        .username(registerRequest.username())
        .password(passwordEncoder.encode(registerRequest.password()))
        .createdAt(Instant.now())
        .roles(List.of(AppUser.UserRoles.ROLE_USER.toString()))
        .build();

    appUserRepository.save(newAppUser);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return null;
  }
}
