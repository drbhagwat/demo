package com.example.demo.service;

import com.example.demo.exception.StudentEmailAlreadyTaken;
import com.example.demo.exception.StudentNotFoundException;
import com.example.demo.model.Student;
import com.example.demo.repo.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public void addStudent(Student student) {
        final String email = student.getEmail();

        try {
            studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException e) {
            throw new StudentEmailAlreadyTaken(email);
        }
    }

    public void deleteStudent(Long id) {
        boolean exists = studentRepository.existsById(id);

        if (!exists) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }

    @Transactional
    public void updateStudent(Long id, Student student) {
        Student dbStudent = studentRepository.findById(id).orElseThrow(
                () -> new StudentNotFoundException(id));

        String name = student.getName();
        String email = student.getEmail();
        LocalDate dob = student.getDob();

        if (name != null && !name.isBlank()
                && !Objects.equals(dbStudent.getName(), name)) {
            dbStudent.setName(name);
        }

        if (email != null && !email.isBlank()
                && !Objects.equals(dbStudent.getEmail(), email)) {
            Optional<Student> optionalStudent = studentRepository.findStudentByEmail(email);

            if (optionalStudent.isPresent()) {
                throw new StudentEmailAlreadyTaken(email);
            }
            dbStudent.setEmail(email);
        }

        if (dob != null && !Objects.equals(dbStudent.getDob(), dob)) {
            dbStudent.setDob(dob);
        }
    }
}
