package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Address extends BasicLogger<String> {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  private Boolean isPrimary;

  @NotBlank(message = "{address1.notblank}")
  private String address1;

  private String address2;

  @NotBlank(message = "{city.notblank}")
  private String city;

  @NotBlank(message = "{state.notblank}")
  private String state;

  @NotBlank(message = "{zip.notblank}")
  private String zip;

/*
  @NotNull(message = "{COUNTRY_MANDATORY}")
  @NotBlank(message = "{COUNTRY_CANNOT_BE_BLANK}")
  private String country;

  @NotNull(message = "{CONTACT_NAME_MANDATORY}")
  @NotBlank(message = "{CONTACT_NAME_CANNOT_BE_BLANK}")
  private String contactName;

  @NotNull(message = "{CONTACT_NUMBER_MANDATORY}")
  @NotBlank(message = "{CONTACT_NUMBER_CANNOT_BE_BLANK}")
  private String contactNumber;
*/

  @ManyToOne
  @JoinColumn
  private Company company;
}
