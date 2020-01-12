package com.example.demo.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeCannotBeBlank extends Exception {
	private static final long serialVersionUID = 1L;
	public CodeCannotBeBlank(String exception) {
    super(exception);
  }
}
