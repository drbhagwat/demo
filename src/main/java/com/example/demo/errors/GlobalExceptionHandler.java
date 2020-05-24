package com.example.demo.errors;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@SuppressWarnings({"unchecked", "rawtypes"})
@Data
@NoArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  @Value("${APP_EXCEPTION}")
  private String appException;

  @Value("${VALIDATION_FAILED}")
  private String validationFailed;

  @ExceptionHandler(Exception.class)
  public final ModelAndView handleAllExceptions(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
        ex.getMessage(), ((ServletWebRequest)request).getRequest().getRequestURI().toString());
    ModelAndView mav = new ModelAndView();
    mav.addObject("errorDetails", errorDetails);
    mav.setViewName("error");
    return mav;
  }
}
