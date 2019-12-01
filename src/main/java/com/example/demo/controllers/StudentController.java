package com.example.demo.controllers;

import com.example.demo.models.Student;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.services.StudentService.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @RequestMapping("/getAll")
    public String getAll(Model model, @RequestParam(defaultValue = "0") Integer pageNo,
                         @RequestParam(defaultValue = "1") Integer pageSize) {

        List<Student> studentList = studentRepository.findAll();

        if (studentList.size() > 0) {
            model.addAttribute("data", studentService.getAll(pageNo, pageSize));
            model.addAttribute("currentPage", pageNo);
        }
        return "studentsView";
    }

    @RequestMapping("/get")
    @ResponseBody
    public Optional<Student> get(int id) {
        return studentService.get(id);
    }

    @PostMapping("/add")
    public String add(Student student) {
        studentService.add(student);
        return "redirect:/students/getAll";
    }

    @DeleteMapping("/delete")
    public String delete(int id) {
        studentService.delete(id);
        return "redirect:/students/getAll";
    }

    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.PUT})
    public String update(Student student) {
        studentService.update(student);
        return "redirect:/students/getAll";
    }
}
