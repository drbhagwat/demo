package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Student {
  @Id
  private UUID id;

  @OneToOne(optional = false)
  @MapsId
  @JoinColumn(name = "id")
  private AppUser appUser;

  @NotBlank(message = "Full name is required")
  @Column(nullable = false)
  private String fullName;

  @NotNull(message = "Date of birth is required")
  @Column(nullable = false)
  private LocalDate dob;

  @Transient
  private Integer age;
}
