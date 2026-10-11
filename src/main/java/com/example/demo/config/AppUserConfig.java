package com.example.demo.config;

import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppUserConfig {
  private final PasswordEncoder passwordEncoder;
  private final AppUserRepository appUserRepository;

  public AppUserConfig(PasswordEncoder passwordEncoder, AppUserRepository appUserRepository) {
    this.passwordEncoder = passwordEncoder;
    this.appUserRepository = appUserRepository;
  }

  @Bean
  CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
    return args -> {
      AppUser admin = new AppUser("dinesh.bhagwat@gmail.com", passwordEncoder.encode("Bha!625822544"));
      appUserRepository.save(admin);
    };
  }
}
