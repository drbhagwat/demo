package com.example.demo.service;

import com.example.demo.dto.RegisterAdminRequest;
import com.example.demo.exception.EmailAlreadyTaken;
import com.example.demo.exception.UserNameAlreadyExistsException;
import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final AppUserRepository appUserRepository;

  public AppUser registerAdmin(RegisterAdminRequest registerAdminRequest) {
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
    return(appUserRepository.save(appUser));
  }

  public List<AppUser> getUsers() {
    return appUserRepository.findAll();
  }

  public AppUser getUser(UUID id) {
    return appUserRepository.findById(id).orElseThrow(()
        -> new UsernameNotFoundException("User with id " + id + " not found"));
  }

  @Transactional
  public void updateUser(UUID id, RegisterAdminRequest registerAdminRequest) {
    AppUser dbAppUser = appUserRepository.findById(id).orElseThrow(
        () -> new UsernameNotFoundException("User with id " + id + " not found"));

    String dbEmail = dbAppUser.getUsername();
    String newEmail = registerAdminRequest.username();
    String newPassword = registerAdminRequest.password();

    if (newEmail != null &&
        !newEmail.isBlank() &&
        !Objects.equals(dbEmail, newEmail)) {
      Optional<AppUser> optionalUser = appUserRepository.findByUsername(newEmail);

      if (optionalUser.isPresent()) {
        throw new EmailAlreadyTaken(newEmail);
      }
      dbAppUser.setUsername(newEmail);
    }

    if (newPassword != null && !newPassword.isBlank()) {
      if (!passwordEncoder.matches(newPassword, dbAppUser.getPassword())) {
        dbAppUser.setPassword(passwordEncoder.encode(newPassword));
      }
    }
  }

  @Transactional
  public void deleteUser(UUID id) {
    AppUser appUser = appUserRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));

    boolean isAdmin = appUser.getRoles()
        .contains(AppUser.UserRoles.ROLE_ADMIN.toString());

    if (isAdmin) {
      long adminCount =
          appUserRepository.countUsersByRole("ROLE_ADMIN");

      if (adminCount <= 1) {
        throw new IllegalStateException(
            "Cannot delete the last admin"
        );
      }
    }
    appUserRepository.delete(appUser);
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