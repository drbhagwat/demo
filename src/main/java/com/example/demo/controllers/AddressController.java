package com.example.demo.controllers;

import com.example.demo.models.Address;
import com.example.demo.models.Company;
import com.example.demo.repositories.AddressRepository;
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
@RequestMapping()
public class AddressController {
  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @GetMapping("/{entity}/{entityId}/addresses")
  public String getAll(@PathVariable String entity,
                       @PathVariable String entityId,
                       @RequestParam(defaultValue = "0") Integer pageNo,
                       @RequestParam(defaultValue = "1") Integer pageSize,
                       @RequestParam(defaultValue = "lastUpdatedDateTime") String sortBy,
                       @RequestParam(defaultValue = "D") String orderBy, Model model) {
    Pageable pageable = orderBy.equals("A") ? PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending())
      : PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
    Page<Address> addresses = addressRepository.findAll(pageable);

    model.addAttribute("entity", entity);
    model.addAttribute("entityId", entityId);

    if (addresses.getTotalPages() == 0) {
      model.addAttribute("addresses", "empty");
    }
    else {
      model.addAttribute("addresses", addresses);
      model.addAttribute("currentPage", pageNo);
    }
    return "address/address-index";
  }

  @GetMapping("/{entity}/{entityId}/addresses/add")
  public String add(@PathVariable String entity,
                    @PathVariable String entityId,
                    Model model) {
    model.addAttribute("entity", entity);
    model.addAttribute("entityId", entityId);
    model.addAttribute("address", new Address());
    return "address/add-address";
  }

  @PostMapping("/{entity}/{entityId}/addresses/add")
  public String add(@PathVariable String entity,
                    @PathVariable String entityId,
                    @Valid Address address, BindingResult result, Model model) throws Exception {
    if (result.hasErrors()) {
      return "address/add-address";
    }
    Optional<Company> existingCompany = companyRepository.findById(entityId);

    if (existingCompany.isEmpty()) {
      throw  new Exception("Invalid Entity Id: " + entityId);
    }
    address.setCompany(existingCompany.get());
    addressRepository.save(address);
    return "redirect:/{entity}/{entityId}/addresses";
  }

  @GetMapping("/{entity}/{entityId}/addresses/update/{addressId}")
  public String update(@PathVariable String entity,
                       @PathVariable String entityId,
                       @PathVariable int addressId,
                       Model model) {
    Address existingAddress = addressRepository.findById(addressId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid address id:" + addressId));

    model.addAttribute("address", existingAddress);
    model.addAttribute("entity", entity);
    model.addAttribute("entityId", entityId);
    return "address/update-address";
  }

  @PostMapping("/{entity}/{entityId}/addresses/update/{addressId}")
  public String update(@PathVariable String entity, @PathVariable String entityId,
                       @PathVariable int addressId, @Valid Address address, BindingResult result, Model model) {
    address.setId(addressId);

    if (result.hasErrors()) {
      return "address/update-address";
    }
    Address newAddress = addressRepository.save(address);
    newAddress.setCompany(companyRepository.findById(entityId).get());
    return "redirect:/{entity}/{entityId}/addresses";
  }

  @GetMapping("/{entity}/{entityId}/addresses/delete/{addressId}")
  public String delete(@PathVariable int addressId, Model model) {
    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid address Id:" + addressId));
    addressRepository.delete(address);
    return "redirect:/{entity}/{entityId}/addresses";
  }
}
