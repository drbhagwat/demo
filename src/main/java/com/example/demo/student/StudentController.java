package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/students")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping()
    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping(path = {"{studentId}"})
    public Student getStudent(@PathVariable("studentId") Long id) {
        return  studentService.getStudent(id);
    }

    @PostMapping()
    public void addStudent(@RequestBody Student student) {
        studentService.addStudent(student);
    }

    @DeleteMapping(path = {"{studentId}"})
    public void deleteStudent(@PathVariable("studentId") Long id) {
        studentService.deleteStudent(id);
    }

    @PutMapping(path = {"{studentId}"})
    public void updateStudent(@PathVariable("studentId") Long id,
                              @RequestBody Student student) {
        studentService.updateStudent(id, student);
    }
}
