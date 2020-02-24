package com.example.demo.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
  @NotBlank(message = "{code.cannotbeblank}")
  @Column(unique=true)
  private String code;

  @NotBlank(message = "{name.cannotbeblank}")
  private String name;

  @NotBlank(message = "{description.cannotbeblank}")
  private String description;

  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Address> addresses = new ArrayList<>();
}