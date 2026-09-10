package com.example.demo.config;

import com.example.demo.model.Student;
import com.example.demo.repo.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {

  @Bean
  CommandLineRunner commandLineRunner(StudentRepository studentRepository) {
    return args -> {
      Student dinesh = new Student("Dinesh Bhagwat", LocalDate.of(1967, Month.JANUARY, 1)
          , "dinesh.bhagwat@gmail.com");
      Student vasanti = new Student("Vasanti Bhagwat", LocalDate.of(1970, Month.DECEMBER, 6)
          , "vasantidin@gmail.com");

      studentRepository.saveAll(List.of(dinesh, vasanti));
    };
  }
}
