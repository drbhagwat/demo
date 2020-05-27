package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.NimbusAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest>
  authorizationRequestRepository() {

    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>
  accessTokenResponseClient() {
    return new NimbusAuthorizationCodeTokenResponseClient();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/css/**", "/images/**", "/error", "/webjars/**", "/", "/loginSuccess").permitAll()
        .anyRequest().authenticated()
        .and()
        .oauth2Login().loginPage("/")
        .defaultSuccessUrl("/loginSuccess", true)
        .authorizationEndpoint()
        .baseUri("/oauth2/authorize-client")
        .authorizationRequestRepository(authorizationRequestRepository()).and()
        .tokenEndpoint()
        .accessTokenResponseClient(accessTokenResponseClient()).and()
        .redirectionEndpoint()
        .baseUri("http://localhost:8080/loginSuccess");
  }
}
