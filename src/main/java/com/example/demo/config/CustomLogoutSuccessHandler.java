package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends
    SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

  @Override
  public void onLogoutSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws IOException, ServletException {
    
    String refererUrl = request.getHeader("Referer");
    LOGGER.info("Logout from: {}", refererUrl);
    super.onLogoutSuccess(request, response, authentication);
  }
}