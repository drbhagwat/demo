package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class ParameterLengthExceedsExpectedLength extends Exception {
	private static final long serialVersionUID = 1L;
	public ParameterLengthExceedsExpectedLength(String exception) {
    super(exception);
  }
}
