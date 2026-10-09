package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


/*
 * AppUser is the applicationUser model class that represents a user in the application.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AppUser implements UserDetails  {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id; // Universal identifier for the user

  @NotBlank(message = "Full name is required")
  @Column(nullable = false)
  private String fullName; // full name of the user

  @NotBlank(message = "Username is required")
  @Column(unique = true, nullable = false)
  private String username; // email of the user and hence unique

  @NotBlank(message = "Password is required")
  @Column(nullable = false)
  private String password; // password of the user

  @NonNull
  private List<String> roles; // a user can have multiple roles

  @Column(nullable = false, updatable = false)
  private Instant createdAt; // timestamp of when the user was created in the application

  @NonNull
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream().map(SimpleGrantedAuthority::new).toList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @NonNull
  @Override
  public String getUsername() {
    return username;
  }

  public enum UserRoles {
    ROLE_USER,
    ROLE_ADMIN;
  }
}
