package com.example.demo.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyAlreadyExists extends Exception {
	private static final long serialVersionUID = 1L;
	public CompanyAlreadyExists(String exception) {
    super(exception);
  }
}
