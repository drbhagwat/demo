package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterStudentRequest;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import com.example.demo.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "${app.api.v1}/students")
@RequiredArgsConstructor
public class StudentController {
  private final StudentService studentService;
  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid RegisterStudentRequest registerStudentRequest) {
    studentService.registerStudent(registerStudentRequest);
    final var loginRequest = new LoginRequest(registerStudentRequest.username(), registerStudentRequest.password());
    return authService.login(loginRequest);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
    var authResponse = authService.login(loginRequest);
    return ResponseEntity.ok(authResponse);
  }

  @GetMapping()
  public List<Student> getStudents() {
    return studentService.getStudents();
  }

  @GetMapping(path = {"{studentId}"})
  public Student getStudent(@PathVariable("studentId") UUID id) {
    return studentService.getStudent(id);
  }

  @PostMapping()
  public void addStudent(@RequestBody Student student) {
    studentService.addStudent(student);
  }

  @DeleteMapping(path = {"{studentId}"})
  public void deleteStudent(@PathVariable("studentId") UUID id) {
    studentService.deleteStudent(id);
  }

  @PutMapping(path = {"{studentId}"})
  public void updateStudent(@PathVariable("studentId") UUID id,
                            @RequestBody @Valid RegisterStudentRequest registerStudentRequest) {
    studentService.updateStudent(id, registerStudentRequest);
  }
}