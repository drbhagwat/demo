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

  private boolean isPrimary;

  @NotNull(message = "{ADDRESS1_MANDATORY}")
  @NotBlank(message = "{ADDRESS1_CANNOT_BE_BLANK}")
  private String address1;

  private String address2;

  @NotNull(message = "{CITY_MANDATORY}")
  @NotBlank(message = "{CITY_CANNOT_BE_BLANK}")
  private String city;

  @NotNull(message = "{STATE_MANDATORY}")
  @NotBlank(message = "{STATE_CANNOT_BE_BLANK}")
  private String state;

  @NotNull(message = "{ZIP_MANDATORY}")
  @NotBlank(message = "{ZIP_CANNOT_BE_BLANK}")
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

  @JsonIgnore
  @ManyToOne
  @JoinColumn
  private Company company;
}
