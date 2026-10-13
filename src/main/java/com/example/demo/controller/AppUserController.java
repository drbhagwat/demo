package com.example.demo.controller;

import com.example.demo.dto.RegisterAdminRequest;
import com.example.demo.model.AppUser;
import com.example.demo.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "${app.api.v1}/users")
@RequiredArgsConstructor
public class AppUserController {
  private final AppUserService appUserService;

  @GetMapping
  public List<AppUser> getUsers() {
    return appUserService.getUsers();
  }

  @GetMapping("/{userId}")
  public AppUser getUser(@PathVariable("userId") UUID id) {
    return appUserService.getUser(id);
  }

  @PutMapping("/{userId}")
  public void updateUser(@PathVariable("userId") UUID id,
                         @RequestBody @Valid RegisterAdminRequest registerAdminRequest) {
    appUserService.updateUser(id, registerAdminRequest);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable("userId") UUID id) {
    appUserService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}