package com.example.demo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterAdminRequest(
    @NotBlank(message = "Username is required")
    @Email(message = "Enter a valid email address")
    String username,

    @NotBlank(message = "Password is required")
    String password
) {}
