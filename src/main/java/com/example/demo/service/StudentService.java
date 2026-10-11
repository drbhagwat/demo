package com.example.demo.service;

import com.example.demo.dto.RegisterStudentRequest;
import com.example.demo.exception.StudentEmailAlreadyTaken;
import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.exception.UserNameAlreadyExistsException;
import com.example.demo.model.AppUser;
import com.example.demo.model.Student;
import com.example.demo.repo.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
  private final PasswordEncoder passwordEncoder;
  private final StudentRepository studentRepository;

  @Autowired
  public StudentService(PasswordEncoder passwordEncoder, StudentRepository studentRepository) {
    this.passwordEncoder = passwordEncoder;
    this.studentRepository = studentRepository;
  }

  public void register(RegisterStudentRequest registerStudentRequest) {
    String username = registerStudentRequest.username();
    Optional<Student> student = studentRepository.findByAppUser_Username(username);

    if (student.isPresent()) {
      throw new UserNameAlreadyExistsException(username);
    }

    var apUser = AppUser.builder()
        .username(username)
        .password(passwordEncoder.encode(registerStudentRequest.password()))
        .createdAt(Instant.now())
        .roles(List.of(AppUser.UserRoles.ROLE_STUDENT.toString()))
        .build();

    var newStudent = Student.builder()
        .appUser(apUser)
        .build();

    studentRepository.save(newStudent);
  }


  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  public Student getStudent(UUID id) {
    return studentRepository.findById(id)
        .orElseThrow(() -> new StudentNotFoundException(id));
  }

  public void addStudent(Student student) {
    final String email = student.getAppUser().getUsername();

    try {
      studentRepository.saveAndFlush(student);
    } catch (DataIntegrityViolationException e) {
      throw new StudentEmailAlreadyTaken(email);
    }
  }

  public void deleteStudent(UUID id) {
    boolean exists = studentRepository.existsById(id);

    if (!exists) {
      throw new StudentNotFoundException(id);
    }
    studentRepository.deleteById(id);
  }

  @Transactional
  public void updateStudent(UUID id, Student student) {
    Student dbStudent = studentRepository.findById(id).orElseThrow(
        () -> new StudentNotFoundException(id));

    AppUser appUser = dbStudent.getAppUser();
    String email = appUser.getUsername();
    LocalDate dob = student.getDob();

    if (email != null && !email.isBlank()
        && !Objects.equals(dbStudent.getAppUser().getUsername(), appUser)) {
      appUser.setUsername(email);
    }

    if (email != null && !email.isBlank()
        && !Objects.equals(appUser.getUsername(), email)) {
      Optional<Student> optionalStudent = studentRepository.findByAppUser_Username(email);

      if (optionalStudent.isPresent()) {
        throw new StudentEmailAlreadyTaken(email);
      }
      appUser.setUsername(email);
    }

    if (dob != null && !Objects.equals(dbStudent.getDob(), dob)) {
      dbStudent.setDob(dob);
    }
  }
}
