package com.example.demo.controller;

import com.example.demo.exception.AppUserNotFoundException;
import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "${app.api.v1}/admin")
@RequiredArgsConstructor
public class AppUserController {
  private final AppUserRepository appUserRepository;

  @DeleteMapping(path = {"{userId}"})
  public ResponseEntity<?> deleteUser(@PathVariable("userId") UUID id) {
    Optional<AppUser> appUser = appUserRepository.findById(id);

    if (appUser.isEmpty()) {
      throw new AppUserNotFoundException(id);
    }
    long adminCount = appUserRepository.countUsersByRole("ROLE_ADMIN");

    if (adminCount <= 1) {
      throw new IllegalStateException("Cannot delete the last admin");
    }
    appUserRepository.delete(appUser.get());
    return ResponseEntity.ok("Admin deleted successfully");
  }
}