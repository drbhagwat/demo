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

import javax.transaction.Transactional;
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
  private static final String UPDATE_ADDRESS = "address/update-address";
  private static final String ADD_ADDRESS = "address/add-address";

  @GetMapping("/{entity}/{entityId}/addresses")
  public String getAll(@PathVariable String entity,
                       @PathVariable String entityId,
                       Model model) {
    List<Address> addresses = addressRepository.findAll();
    model.addAttribute(ENTITY, entity);
    model.addAttribute(ENTITY_ID, entityId);

    if (addresses.isEmpty()) {
      model.addAttribute("addresses", "empty");
    } else {
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
    return ADD_ADDRESS;
  }


  @Transactional
  @PostMapping("/{entity}/{entityId}/addresses/add")
  public String add(@PathVariable String entity,
                    @PathVariable String entityId,
                    @Valid Address address, BindingResult result) throws Exception {

    if (result.hasErrors()) {
      return ADD_ADDRESS;
    }
    Company existingCompany = companyRepository.findById(entityId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid Entity Id:" + entityId));
    address.setCompany(existingCompany);

    // get the list of current addresses from the db.
    List<Address> addresses = addressRepository.findAll();

    // no address in db and current address both are not primary
    if (isNoAddressPrimary(addresses) && (!address.getIsPrimary())) {
      result.rejectValue("isPrimary", "isPrimary.missing");
      return ADD_ADDRESS;
    }
    // if current address is primary
    if (address.getIsPrimary()) {
      makeExistingPrimaryAsSecondary(addresses);
    }
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
    return UPDATE_ADDRESS;
  }

  @Transactional
  @PostMapping("/{entity}/{entityId}/addresses/update/{addressId}")
  public String update(@PathVariable String entity, @PathVariable String entityId,
                       @PathVariable int addressId, @Valid Address address, BindingResult result) throws Exception {
    address.setId(addressId);

    if (result.hasErrors()) {
      return UPDATE_ADDRESS;
    }
    Company existingCompany = companyRepository.findById(entityId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid Entity Id:" + entityId));
    address.setCompany(existingCompany);

    // get the list of current addresses from the db.
    List<Address> addresses = addressRepository.findAll();

    // only one address in db (current one) which is not primary
    if ( (addresses.size() == 1) && (!address.getIsPrimary())) {
      result.rejectValue("isPrimary", "isPrimary.missing");
      return UPDATE_ADDRESS;
    }

    // no address in db and current address both are not primary
    if (isNoAddressPrimary(addresses) && (!address.getIsPrimary())) {
      result.rejectValue("isPrimary", "isPrimary.missing");
      return ADD_ADDRESS;
    }

    // if the current address is mentioned as primary
    if (address.getIsPrimary()) {
      makeExistingPrimaryAsSecondary(addresses);
    }
    addressRepository.save(address);
    return ADDRESS_HOME;
  }

  @Transactional
  @GetMapping("/{entity}/{entityId}/addresses/delete/{addressId}")
  public String delete(@PathVariable int addressId, Model model) throws Exception {
    Address address = addressRepository.findById(addressId)
      .orElseThrow(() -> new IllegalArgumentException("Invalid address Id:" + addressId));

    // get the list of current addresses from the db.
    List<Address> addresses = addressRepository.findAll();
    addresses.remove(address);

    // if there are other addresses in db and none of them is primary
    if (((addresses.size() != 0)) && isNoAddressPrimary(addresses)) {
      return "address/delete-address";
    }
    addressRepository.delete(address);
    return ADDRESS_HOME;
  }

  private boolean isNoAddressPrimary(List<Address> addresses) {
    int size = addresses.size();
    int totalPrimaryAddresses = 0;

    for (int i = 0; i < size; i++) {

      if (addresses.get(i).getIsPrimary().booleanValue()) {
        totalPrimaryAddresses++;
      }
    }
    return totalPrimaryAddresses == 0;
  }

  @Transactional
  private void makeExistingPrimaryAsSecondary(List<Address> addresses) {
    int size = addresses.size();

    for (int i = 0; i < size; i++) {
      Address address = addresses.get(i);

      // there is one address in address book which should be primary; change it to secondary
      if (address.getIsPrimary().booleanValue()) {
        address.setIsPrimary(false);
        addressRepository.save(address);
        return;
      }
    }
  }
}
