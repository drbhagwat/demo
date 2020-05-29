package com.example.demo.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class GetUser {
  @GetMapping("/user")
  public String user(@AuthenticationPrincipal OAuth2User principal) {
    return principal.getAttribute("name");
  }
}
