package com.example.demo.validators;

/**
 * 
 * This is an interface to validate the special characters of the string.
 * 
 * @author : Dinesh Bhagwat
 * @version : 1.0
 * @since : 2019-11-04
 *
 */
interface AlphaNumericValidator {
  default void validate(String value) throws Exception {

    if (!value.matches("^[ a-zA-Z0-9]*$")) {
      throw new Exception();
    }
  }
}
