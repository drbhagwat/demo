package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/")
public class LoginSuccess {
  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  @GetMapping("/loginSuccess")
  public String getLoginInfo(Model model) {
    Authentication authentication =
        SecurityContextHolder
            .getContext()
            .getAuthentication();

    OAuth2AuthenticationToken oauthToken =
        (OAuth2AuthenticationToken) authentication;

    OAuth2AuthorizedClient client = authorizedClientService
        .loadAuthorizedClient(
            oauthToken.getAuthorizedClientRegistrationId(),
            oauthToken.getName());

    String userInfoEndpointUri = client.getClientRegistration()
        .getProviderDetails().getUserInfoEndpoint().getUri();

    if (!StringUtils.isEmpty(userInfoEndpointUri)) {
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
          .getTokenValue());
      HttpEntity entity = new HttpEntity("", headers);
      ResponseEntity<Map> response = restTemplate
          .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
      Map userAttributes = response.getBody();
      model.addAttribute("user", userAttributes.get("name"));
    }

    return "login-success";
  }
}
