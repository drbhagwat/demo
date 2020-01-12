package com.example.demo.validators;

/**
 * This is an interface provides length check for the given strings
 *
 * @author : Dinesh Bhagwat
 * @version : 1.0
 * @since : 2019-11-23
 */

interface LengthValidator {
  default void validate(int currentLength, int expectedLength) throws Exception {
    if (currentLength > expectedLength) {
      throw new Exception();
    }
  }
}
