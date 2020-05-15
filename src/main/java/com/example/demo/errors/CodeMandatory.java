package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class CodeMandatory extends Exception {
	private static final long serialVersionUID = 1L;
	public CodeMandatory(String exception) {
    super(exception);
  }
}
