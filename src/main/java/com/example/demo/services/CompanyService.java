package com.example.demo.services;

import com.example.demo.errors.CompanyAlreadyExists;
import com.example.demo.errors.CompanyNotFound;
import com.example.demo.models.Company;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class CompanyService {
  @Value("${company.already.exists}")
  private String companyAlreadyExists;

  @Value("${company.not.found}")
  private String companyNotFound;

  @Autowired
  private CompanyRepository companyRepository;

  public Page<Company> getAll(Integer pageNo, Integer pageSize,
                              String sortBy, String orderBy) {
    Pageable pageable = orderBy.equals("A") ? PageRequest.of(pageNo, pageSize
        , Sort.by(sortBy).ascending())
        : PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    return companyRepository.findAll(pageable);
  }

  public Company add(Company company) throws CompanyAlreadyExists {
    String companyCode = company.getCode();
    Optional<Company> tempCompany = companyRepository.findById(companyCode);

    if (tempCompany.isPresent()) {
      throw new CompanyAlreadyExists(companyAlreadyExists);
    }
    company.setCode(companyCode);
    return companyRepository.save(company);
  }

  public Company update(@PathVariable String companyCode) throws CompanyNotFound {
    Optional<Company> optionalCompany = companyRepository.findById(companyCode);

    if (optionalCompany.isEmpty()) {
      throw new CompanyNotFound(companyNotFound);
    }
    return optionalCompany.get();
  }

  public void update(Company company) {
    companyRepository.save(company);
  }

  public void delete(String companyCode) throws CompanyNotFound {
    Optional<Company> optionalCompany = companyRepository.findById(companyCode);

    if (optionalCompany.isEmpty()) {
      throw new CompanyNotFound(companyNotFound);
    }
    companyRepository.delete(optionalCompany.get());
  }
}