package com.example.demo.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class CarrierShipmentServiceCannotContainSpecialCharacters extends Exception {
    private static final long serialVersionUID = 1L;

    public CarrierShipmentServiceCannotContainSpecialCharacters(String exception) {
        super(exception);
    }
}