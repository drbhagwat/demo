/*
package com.example.demo.api.services;

import com.example.demo.api.exceptions.CompanyAlreadyExists;
import com.example.demo.api.exceptions.CompanyNotFound;
import com.example.demo.api.models.Company;
import com.example.demo.api.repositories.CompanyRepository;
import com.example.demo.api.validators.CompanyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class CompanyService {
  @Value("${COMPANY_NOT_FOUND}")
  private String companyNotFound;

  @Value("${COMPANY_ALREADY_EXISTS}")
  private String companyAlreadyExists;

  @Autowired
  private CompanyValidator companyValidator;

  @Autowired
  private CompanyRepository companyRepository;

  public Page<Company> getAll(Integer pageNo, Integer pageSize, String sortBy, String orderBy) {
    Pageable pageable = orderBy.equals("A") ? PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending())
      : PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    return companyRepository.findAll(pageable);
  }

  public Company get(String companyCode) throws Exception {
    Optional<Company> company = companyRepository.findById(companyCode);

    if (company.isEmpty()) {
      throw new CompanyNotFound(companyNotFound);
    }
    return company.get();
  }

  public Company add(Company company) throws Exception {
    String companyCode = company.getCode();
    companyValidator.validate(companyCode);
    Optional<Company> tempCompany = companyRepository.findById(companyCode);

    if (!tempCompany.isEmpty()) {
      throw new CompanyAlreadyExists(companyAlreadyExists);
    }
    company.setCode(companyCode);
    return companyRepository.save(company);
  }

  public Company update(String companyCode, Company company) throws Exception {
    companyValidator.validate(companyCode);
    Company existingCompany = get(companyCode);
    existingCompany.setName(company.getName());
    existingCompany.setDescription(company.getDescription());
    return companyRepository.save(existingCompany);
  }

  public void delete(String companyCode) throws Exception {
    Company company = get(companyCode);
    companyRepository.delete(company);
  }
}
*/
