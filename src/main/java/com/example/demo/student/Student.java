package com.example.demo.student;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private LocalDate dob;

    @Transient
    private Integer age;
    private String email;

   public Student(String name, LocalDate dob, String email) {
       this.name = name;
       this.dob = dob;
       this.email = email;
   }

   public Integer getAge() {
       return(Period.between(this.dob, LocalDate.now()).getYears());
   }
}
