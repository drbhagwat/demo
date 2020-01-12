/*
package com.example.demo.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CompanyValidator {
  @Value("${COMPANY_CODE_MAX_LENGTH}")
  private int companyCodeMaxLength;

  @Autowired
  private CodeValidator codeValidator;

  public void validate(String code) throws Exception {
    codeValidator.validate(code);
    codeValidator.validate(code.length(), companyCodeMaxLength);
  }
}*/
