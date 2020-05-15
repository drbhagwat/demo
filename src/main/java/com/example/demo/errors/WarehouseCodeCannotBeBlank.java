package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class WarehouseCodeCannotBeBlank extends Exception {
	private static final long serialVersionUID = 1L;
	public WarehouseCodeCannotBeBlank(String exception) {
    super(exception);
  }
}
