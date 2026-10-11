package com.example.demo.service;

import com.example.demo.dto.RegisterStudentRequest;
import com.example.demo.exception.StudentEmailAlreadyTaken;
import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.exception.UserNameAlreadyExistsException;
import com.example.demo.model.AppUser;
import com.example.demo.model.Student;
import com.example.demo.repo.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StudentService {
  private final PasswordEncoder passwordEncoder;
  private final StudentRepository studentRepository;

  public void registerStudent(RegisterStudentRequest registerStudentRequest) {
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
        .dob(registerStudentRequest.dob())
        .fullName(registerStudentRequest.fullName())
        .build();
    studentRepository.save(newStudent);
  }

  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  public Student getStudent(UUID id) {
    return studentRepository
        .findById(id)
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
  public void updateStudent(UUID id, RegisterStudentRequest registerStudentRequest) {
    Student dbStudent = studentRepository.findById(id).orElseThrow(
        () -> new StudentNotFoundException(id));

    AppUser dbAppUser = dbStudent.getAppUser();
    String dbEmail = dbAppUser.getUsername();
    String dbFullName = dbStudent.getFullName();
    LocalDate dbDob = dbStudent.getDob();

    String newPassword = registerStudentRequest.password();
    LocalDate dob = registerStudentRequest.dob();
    String newEmail = registerStudentRequest.username();
    String fullName = registerStudentRequest.fullName();

    if (newEmail != null &&
        !newEmail.isBlank() &&
        !Objects.equals(dbEmail, newEmail)) {
      Optional<Student> optionalStudent = studentRepository.findByAppUser_Username(newEmail);

      if (optionalStudent.isPresent()) {
        throw new StudentEmailAlreadyTaken(newEmail);
      }
      dbAppUser.setUsername(newEmail);
    }

    if (dob != null && !Objects.equals(dbDob, dob)) {
      dbStudent.setDob(dob);
    }

    if (fullName != null &&
        !fullName.isBlank() &&
        !Objects.equals(dbFullName, fullName)) {
      dbStudent.setFullName(registerStudentRequest.fullName());
    }

    if (newPassword != null && !newPassword.isBlank()) {
      dbAppUser.setPassword(passwordEncoder.encode(newPassword));
    }
  }
}