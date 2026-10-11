package com.example.demo.config;

import com.example.demo.model.AppUser;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppUserConfig {
  private final AppUserRepository appUserRepository;

  public AppUserConfig(AppUserRepository appUserRepository) {
    this.appUserRepository = appUserRepository;
  }

  @Bean
  CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
    return args -> {
      AppUser admin = new AppUser("dinesh.bhgawat@gmail.com", "Bha!625822544");
      appUserRepository.save(admin);
    };
  }
}
