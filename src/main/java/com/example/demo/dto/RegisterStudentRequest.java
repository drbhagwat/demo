package com.example.demo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegisterStudentRequest(
    @NotBlank(message = "Username is required")
    @Email(message = "Enter a valid email address")
    String username,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Full name is required")
    String fullName,

    @NotNull(message = "Date of birth is required")
    LocalDate dob
) {}
