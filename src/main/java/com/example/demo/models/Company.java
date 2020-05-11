package com.example.demo.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BasicLogger<String> {
  @Id
  @NotBlank(message = "{company.code.cannotbeblank}")
  private String code;

  @NotBlank(message = "{company.name.cannotbeblank}")
  private String name;

  @NotBlank(message = "{company.description.cannotbeblank}")
  private String description;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "company_code")
  private List<Address> addresses = new ArrayList<>();
}