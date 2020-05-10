package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class AddressKey implements Serializable {
  @NotBlank(message = "{address.cannotbeblank}")
  private String address;

  @NotBlank(message = "{city.cannotbeblank}")
  private String city;

  @NotBlank(message = "{state.cannotbeblank}")
  private String state;

  @NotBlank(message = "{zip.cannotbeblank}")
  private String zip;
}
