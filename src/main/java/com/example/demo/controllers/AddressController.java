package com.example.demo.controllers;

import com.example.demo.models.Address;
import com.example.demo.models.Company;
import com.example.demo.repositories.AddressRepository;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class AddressController {
  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private static final String ADDRESS_HOME = "redirect:/{entity}/{entityId}/addresses";
  private static final String ENTITY = "entity";
  private static final String ENTITY_ID = "entityId";

  @GetMapping("/{entity}/{entityId}/addresses")
  public String getAll(@PathVariable String entity,
                       @PathVariable String entityId,
                       Model model) {
    List<Address> addresses = addressRepository.findAll();
    model.addAttribute(ENTITY, entity);
    model.addAttribute(ENTITY_ID, entityId);

    if (addresses.isEmpty()) {
      model.addAttribute("addresses", "empty");
    }
    else {
      model.addAttribute("addresses", addresses);
    }
    return "address/address-index";
  }

  @GetMapping("/{entity}/{entityId}/addresses/add")
  public String add(@PathVariable String entity,
                    @PathVariable String entityId,
                    Model model) {
    model.addAttribute(ENTITY, entity);
    model.addAttribute(ENTITY_ID, entityId);
    model.addAttribute("address", new Address());
    return "address/add-address";
  }

  @PostMapping("/{entity}/{entityId}/addresses/add")
  public String add(@PathVariable String entity,
                    @PathVariable String entityId,
                    @Valid Address address, BindingResult result) throws Exception {
    if (result.hasErrors()) {
      return "address/add-address";
    }
    Company existingCompany = companyRepository.findById(entityId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid Entity Id:" + entityId));
    address.setCompany(existingCompany);
    addressRepository.save(address);
    return ADDRESS_HOME;
  }

  @GetMapping("/{entity}/{entityId}/addresses/update/{addressId}")
  public String update(@PathVariable String entity,
                       @PathVariable String entityId,
                       @PathVariable int addressId,
                       Model model) {
    Address existingAddress = addressRepository.findById(addressId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid address Id:" + addressId));
    model.addAttribute("address", existingAddress);
    model.addAttribute(ENTITY, entity);
    model.addAttribute(ENTITY_ID, entityId);
    return "address/update-address";
  }

  @PostMapping("/{entity}/{entityId}/addresses/update/{addressId}")
  public String update(@PathVariable String entity, @PathVariable String entityId,
                       @PathVariable int addressId, @Valid Address address, BindingResult result) throws Exception {
    address.setId(addressId);

    if (result.hasErrors()) {
      return "address/update-address";
    }
    Company existingCompany = companyRepository.findById(entityId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid Entity Id:" + entityId));
    address.setCompany(existingCompany);
    addressRepository.save(address);
    return ADDRESS_HOME;
  }

  @GetMapping("/{entity}/{entityId}/addresses/delete/{addressId}")
  public String delete(@PathVariable int addressId, Model model) {
    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid address Id:" + addressId));
    addressRepository.delete(address);
    return ADDRESS_HOME;
  }
}
