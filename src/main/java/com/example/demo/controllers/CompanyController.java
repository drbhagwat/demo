package com.example.demo.controllers;

import com.example.demo.errors.CompanyAlreadyExists;
import com.example.demo.errors.CompanyNotFound;
import com.example.demo.models.Company;
import com.example.demo.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/companies")
public class CompanyController {
  @Autowired
  private CompanyService companyService;

  private static final String COMPANY = "company";
  private static final String COMPANIES = "companies";
  private static final String COMPANY_HOME = "redirect:/companies";
  private static final String COMPANY_INDEX = "company/company-index";
  private static final String COMPANY_UPDATE_COMPANY = "company/update-company";
  private static final String COMPANY_ADD_COMPANY = "company/add-company";

  @GetMapping()
  public String getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                              @RequestParam(defaultValue = "1") Integer pageSize,
                              @RequestParam(defaultValue =
                                  "lastUpdatedDateTime") String sortBy,
                              @RequestParam(defaultValue = "D") String orderBy,
                              Model model) {
    Page<Company> companies = companyService.getAll(pageNo, pageSize, sortBy,
        orderBy);

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
  public String add(@ModelAttribute @Valid Company company,
                    BindingResult result,
                    Model model) throws CompanyAlreadyExists {
    if (result.hasErrors()) {
      return COMPANY_ADD_COMPANY;
    }
    companyService.add(company);
    return COMPANY_HOME;
  }

  @GetMapping("/update/{companyCode}")
  public String update(@PathVariable String companyCode, Model model) throws CompanyNotFound {
    model.addAttribute(COMPANY, companyService.update(companyCode));
    return COMPANY_UPDATE_COMPANY;
  }

  @PostMapping("/update/{companyCode}")
  public String update(@PathVariable String companyCode,
                       @Valid Company company, BindingResult result) {
    if (result.hasErrors()) {
      return COMPANY_UPDATE_COMPANY;
    }
    companyService.update(company);
    return COMPANY_HOME;
  }

  @GetMapping("/delete/{companyCode}")
  public String delete(@PathVariable String companyCode) throws CompanyNotFound {
    companyService.delete(companyCode);
    return COMPANY_HOME;
  }
}