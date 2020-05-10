package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Address extends BasicLogger<String> {
  @Id
  @GeneratedValue
  private int id;

  private Boolean isPrimary;

  @NotBlank(message = "{address.cannotbeblank}")
  private String address;

  private String address2;

  @NotBlank(message = "{city.cannotbeblank}")
  private String city;

  @NotBlank(message = "{state.cannotbeblank}")
  private String state;

  @NotBlank(message = "{zip.cannotbeblank}")
  private String zip;

  @ManyToOne
  @JoinColumn
  private Company company;
}
