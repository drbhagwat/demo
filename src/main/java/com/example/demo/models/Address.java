package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Address extends BasicLogger<String> {
  @EmbeddedId
  private AddressKey addressKey;

  private Boolean isPrimary;

  private String address2;

  @ManyToOne
  @JoinColumn
  private Company company;
}
