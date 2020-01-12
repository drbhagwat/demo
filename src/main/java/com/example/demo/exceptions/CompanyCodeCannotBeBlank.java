package com.example.demo.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyCodeCannotBeBlank extends Exception {
	private static final long serialVersionUID = 1L;
	public CompanyCodeCannotBeBlank(String exception) {
    super(exception);
  }
}
