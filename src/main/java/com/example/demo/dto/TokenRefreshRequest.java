package com.example.demo.dto;

public record TokenRefreshRequest(String accessToken, String refreshToken) {
}
