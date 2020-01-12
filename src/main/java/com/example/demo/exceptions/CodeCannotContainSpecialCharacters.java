package com.example.demo.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeCannotContainSpecialCharacters extends Exception {
	private static final long serialVersionUID = 1L;
	public CodeCannotContainSpecialCharacters(String exception) {
    super(exception);
  }
}
