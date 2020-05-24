package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class CompanyAlreadyExists extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public CompanyAlreadyExists(String exception) {
    super(exception);
  }
}
