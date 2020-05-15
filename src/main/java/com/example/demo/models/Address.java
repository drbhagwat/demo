package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Company company;
}
