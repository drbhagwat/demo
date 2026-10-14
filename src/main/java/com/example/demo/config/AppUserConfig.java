package com.example.demo.config;

import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;

@Configuration
public class AppUserConfig {
  private final PasswordEncoder passwordEncoder;
  private final AppUserRepository appUserRepository;

  public AppUserConfig(PasswordEncoder passwordEncoder, AppUserRepository appUserRepository) {
    this.passwordEncoder = passwordEncoder;
    this.appUserRepository = appUserRepository;
  }

  @Bean
  CommandLineRunner commandLineRunner() {
    return args -> {
      String username = "dinesh.bhagwat@gmail.com";

      if (appUserRepository.findByUsername(username).isEmpty()) {
        AppUser admin = AppUser.builder()
            .username(username)
            .password(passwordEncoder.encode(System.getenv("DEFAULT_ADMIN_PASSWORD")))
            .createdAt(Instant.now())
            .roles(List.of(
                AppUser.UserRoles.ROLE_ADMIN.toString()))
            .build();

        appUserRepository.save(admin);
      }
    };
  }
}
