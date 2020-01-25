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
import java.util.Optional;

@Controller
@RequestMapping("/companies")
public class CompanyController {
  @Autowired
  private CompanyRepository companyRepository;

  private static final String COMPANY = "company";
  private static final String COMPANIES = "companies";
  private static final String COMPANY_CODE = "companyCode";
  private static final String COMPANY_HOME = "redirect:/companies";
  private static final String COMPANY_EXISTS = "company/company-exists";
  private static final String COMPANY_NOT_FOUND = "company/company-not-found";
  private static final String COMPANY_UPDATE_COMPANY= "company/update-company";
  private static final String COMPANY_ADD_COMPANY= "company/add-company";
  private static final String COMPANY_INDEX= "company/company-index";

  @GetMapping()
  public String getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                       @RequestParam(defaultValue = "1") Integer pageSize,
                       @RequestParam(defaultValue = "lastUpdatedDateTime") String sortBy,
                       @RequestParam(defaultValue = "D") String orderBy, Model model) {
    Pageable pageable = orderBy.equals("A") ? PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending())
      : PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    Page<Company> companies = companyRepository.findAll(pageable);

    if (companies.getTotalPages() == 0) {
      model.addAttribute(COMPANIES, "empty");
    } else {
      model.addAttribute(COMPANIES, companies);
      model.addAttribute("currentPage", pageNo);
    }
    return COMPANY_INDEX;
  }

  @GetMapping("/add")
  public String add(Model model) {
    model.addAttribute(COMPANY, new Company());
    return COMPANY_ADD_COMPANY;
  }

  @PostMapping("/add")
  public String add(@Valid Company company, BindingResult result, Model model) {
    String companyCode = company.getCode();
    Optional<Company> tempCompany = companyRepository.findById(companyCode);

    if (!tempCompany.isEmpty()) {
      model.addAttribute(COMPANY_CODE, companyCode);
      return COMPANY_EXISTS;
    }

    if (result.hasErrors()) {
      return COMPANY_ADD_COMPANY;
    }
    companyRepository.save(company);
    return COMPANY_HOME;
  }

  @GetMapping("/update/{companyCode}")
  public String update(@PathVariable String companyCode, Model model) {
    Optional<Company> tempCompany = companyRepository.findById(companyCode);

    if (tempCompany.isEmpty()) {
      model.addAttribute(COMPANY_CODE, companyCode);
      return COMPANY_NOT_FOUND;
    }
    model.addAttribute(COMPANY, tempCompany.get());
    return COMPANY_UPDATE_COMPANY;
  }

  @PostMapping("/update/{companyCode}")
  public String update(@PathVariable String companyCode, @Valid Company company, BindingResult result) {
    if (result.hasErrors()) {
      return COMPANY_UPDATE_COMPANY;
    }
    companyRepository.save(company);
    return COMPANY_HOME;
  }

  @GetMapping("/delete/{companyCode}")
  public String delete(@PathVariable String companyCode, Model model) {
    Optional<Company> tempCompany = companyRepository.findById(companyCode);

    if (tempCompany.isEmpty()) {
      model.addAttribute(COMPANY_CODE, companyCode);
      return COMPANY_NOT_FOUND;
    }
    companyRepository.delete(tempCompany.get());
    return COMPANY_HOME;
  }
}