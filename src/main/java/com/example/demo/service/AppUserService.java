package com.example.demo.service;

import com.example.demo.dto.RegisterAdminRequest;
import com.example.demo.exception.UserNameAlreadyExistsException;
import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final AppUserRepository appUserRepository;

  public void register(RegisterAdminRequest registerAdminRequest) {
    String username = registerAdminRequest.username();
    Optional<AppUser> dbAppUser = appUserRepository.findByUsername(username);

    if (dbAppUser.isPresent()) {
      throw new UserNameAlreadyExistsException(username);
    }
    var appUser = AppUser.builder()
        .username(username)
        .password(passwordEncoder.encode(registerAdminRequest.password()))
        .createdAt(Instant.now())
        .roles(List.of(AppUser.UserRoles.ROLE_ADMIN.toString()))
        .build();

    appUserRepository.save(appUser);
  }

  @Override
  public @NonNull UserDetails loadUserByUsername(@NonNull String username)
      throws UsernameNotFoundException {

    return appUserRepository.findByUsername(username)
        .orElseThrow(() ->
            new UsernameNotFoundException(
                "User " + username + " not found"
            )
        );
  }
}