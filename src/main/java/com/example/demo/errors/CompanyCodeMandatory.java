package com.example.demo.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class CompanyCodeMandatory extends Exception {
	private static final long serialVersionUID = 1L;
	public CompanyCodeMandatory(String exception) {
    super(exception);
  }
}
