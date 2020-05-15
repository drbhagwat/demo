package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class CompanyNotFound extends Exception {
	private static final long serialVersionUID = 1L;
	public CompanyNotFound(String exception) {
    super(exception);
  }
}
