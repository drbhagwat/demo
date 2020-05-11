package com.example.demo.controllers;

import com.example.demo.models.Address;
import com.example.demo.models.AddressKey;
import com.example.demo.models.Company;
import com.example.demo.repositories.AddressRepository;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@Controller
public class AddressController {
  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private static final String ADDRESS_HOME = "redirect:/{entity}/{entityId" +
      "}/addresses";
  private static final String ENTITY = "entity";
  private static final String ENTITY_ID = "entityId";
  private static final String ADDRESS_KEY = "addressKey";
  private static final String UPDATE_ADDRESS = "address/update-address";
  private static final String ADD_ADDRESS = "address/add-address";

  @GetMapping(value = "/{entity}/{entityId}/addresses")
  public String getAll(@PathVariable("entity") String entity,
                       @PathVariable("entityId") String entityId,
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
  public String add(@PathVariable("entity") String entity,
                    @PathVariable("entityId") String entityId,
                    Model model) {
    model.addAttribute(ENTITY, entity);
    model.addAttribute(ENTITY_ID, entityId);
    model.addAttribute("address", new Address());
    return ADD_ADDRESS;
  }


  @Transactional
  @PostMapping("/{entity}/{entityId}/addresses/add")
  public String add(@PathVariable("entity") String entity,
                    @PathVariable("entityId") String entityId,
                    @Valid Address address,
                    BindingResult result) throws Exception {

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

  @GetMapping("/{entity}/{entityId}/addresses/update/{addressKey}")
  public String update(@PathVariable("entity") String entity,
                       @PathVariable("entityId") String entityId,
                       @PathVariable("addressKey") AddressKey addressKey,
                       Model model) {
    Address existingAddress = addressRepository.findById(addressKey)
        .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
            "key:" + addressKey));
    model.addAttribute("address", existingAddress);
    model.addAttribute(ENTITY, entity);
    model.addAttribute(ENTITY_ID, entityId);
//    model.addAttribute(ADDRESS_KEY, addressKey);
    return UPDATE_ADDRESS;
  }

  @Transactional
  @PostMapping("/{entity}/{entityId}/addresses/update/{addressKey}")
  public String update(@PathVariable("entity") String entity,
                       @PathVariable("entityId") String entityId,
                       @PathVariable("addressKey") AddressKey addressKey,
                       @ModelAttribute @Valid Address address,
                       BindingResult result) {
    address.setAddressKey(addressKey);

    if (result.hasErrors()) {
      return UPDATE_ADDRESS;
    }
    Company existingCompany = companyRepository.findById(entityId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid Entity Id:" + entityId));
    address.setCompany(existingCompany);

    // get the list of current addresses from the db.
    List<Address> addresses = addressRepository.findAll();

    // only one address in db (current one) which is not primary
    if ((addresses.size() == 1) && (!address.getIsPrimary())) {
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
  @GetMapping(value ="/{entity}/{entityId}/addresses/delete/{addressKey}")
  public String delete(@PathVariable("entity") String entity,
                       @PathVariable("entityId") String entityId,
                       @PathVariable AddressKey addressKey) {
    Address address = addressRepository.findById(addressKey)
        .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
            "key:" + addressKey));

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

      // there is one address in address book which should be primary; change
      // it to secondary
      if (address.getIsPrimary().booleanValue()) {
        address.setIsPrimary(false);
        addressRepository.save(address);
        return;
      }
    }
  }
}
