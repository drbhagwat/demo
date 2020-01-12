package com.example.demo.controllers;

import com.example.demo.models.Company;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/companies")
public class CompanyController {
  @Autowired
  private CompanyRepository companyRepository;

  @GetMapping()
  public String getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                       @RequestParam(defaultValue = "1") Integer pageSize,
                       @RequestParam(defaultValue = "lastUpdatedDateTime") String sortBy,
                       @RequestParam(defaultValue = "D") String orderBy, Model model) {
    Pageable pageable = orderBy.equals("A") ? PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending())
      : PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    Page<Company> companies = companyRepository.findAll(pageable);

    if (companies.getTotalPages() == 0) {
      model.addAttribute("companies", "empty");
    }
    else {
      model.addAttribute("companies", companies);
      model.addAttribute("currentPage", pageNo);
    }
    return "company/company-index";
  }

  @GetMapping("/add")
  public String add(Company company) {
    return "company/add-company";
  }

  @PostMapping("/add")
  public String add(@Valid Company company, BindingResult result, Model model) {
    if (result.hasErrors()) {
      return "company/add-company";
    }
    companyRepository.save(company);
    return "redirect:/companies";
  }

  @GetMapping("/update/{companyCode}")
  public String update(@PathVariable String companyCode, Model model) {
    Company company = companyRepository.findById(companyCode)
      .orElseThrow(() -> new IllegalArgumentException("Invalid Company code:" + companyCode));
    model.addAttribute("company", company);
    return "company/update-company";
  }

  @PostMapping("/update/{companyCode}")
  public String update(@PathVariable String companyCode, @Valid Company company, BindingResult result, Model model) {
    if (result.hasErrors()) {
      company.setCode(companyCode);
      return "company/update-company";
    }
    companyRepository.save(company);
    return "redirect:/companies";
  }

  @GetMapping("/delete/{companyCode}")
  public String delete(@PathVariable String companyCode, Model model) {
    Company company = companyRepository.findById(companyCode)
      .orElseThrow(() -> new IllegalArgumentException("Invalid Company code:" + companyCode));
    companyRepository.delete(company);
    return "redirect:/companies";
  }
}