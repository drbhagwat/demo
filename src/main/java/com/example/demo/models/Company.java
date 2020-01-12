package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BasicLogger<String> {
  @Id
  @NotBlank(message = "{code.notblank}")
  private String code;

  @NotBlank(message = "{name.notblank}")
  private String name;

  @NotBlank(message = "{description.notblank}")
  private String description;

/*
  @JsonIgnore
  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Address> addresses = new ArrayList<>();
*/
}