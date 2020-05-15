package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class DivisionCodeCannotContainSpecialCharacters extends Exception {
	private static final long serialVersionUID = 1L;
	public DivisionCodeCannotContainSpecialCharacters(String exception) {
    super(exception);
  }
}
