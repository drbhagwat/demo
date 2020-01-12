/*
package com.example.demo.validators;

import com.example.demo.exceptions.CodeCannotContainSpecialCharacters;
import com.example.demo.exceptions.MaxLengthExceeded;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodeValidator implements LengthValidator, AlphaNumericValidator {
  @Value("${CODE_CANNOT_CONTAIN_SPECIAL_CHARACTERS}")
  private String codeCannotContainSpecialCharacters;

  @Value("${CODE_INVALID_LENGTH}")
  private String codeInvalidLength;

  public void validate(String code) throws Exception {
    try {
      AlphaNumericValidator.super.validate(code);
    } catch (Exception e) {
      throw new CodeCannotContainSpecialCharacters(codeCannotContainSpecialCharacters);
    }
  }

  public void validate(int currentLength, int expectedLength) throws MaxLengthExceeded {
    try {
      LengthValidator.super.validate(currentLength, expectedLength);
    } catch (Exception e) {
      throw new MaxLengthExceeded(codeInvalidLength);
    }
  }
}*/
