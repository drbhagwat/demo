package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterStudentRequest;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import com.example.demo.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    return authService.login(new LoginRequest(registerStudentRequest.username(), registerStudentRequest.password()));
  }

  @GetMapping
  public List<Student> getStudents() {
    return studentService.getStudents();
  }

  @GetMapping("/{studentId}")
  public Student getStudent(@PathVariable("studentId") UUID id) {
    return studentService.getStudent(id);
  }

  @DeleteMapping("/{studentId}")
  public void deleteStudent(@PathVariable("studentId") UUID id) {
    studentService.deleteStudent(id);
  }

  @PutMapping("/{studentId}")
  public void updateStudent(@PathVariable("studentId") UUID id,
                            @RequestBody @Valid RegisterStudentRequest registerStudentRequest) {
    studentService.updateStudent(id, registerStudentRequest);
  }
}