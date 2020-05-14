package com.example.demo.controllers;

import com.example.demo.models.Address;
import com.example.demo.models.AddressKey;
import com.example.demo.models.Company;
import com.example.demo.repositories.AddressRepository;
import com.example.demo.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/addresses")
public class AddressController {
  @Autowired
  private AddressRepository addressRepository;

  @Autowired
  private CompanyRepository companyRepository;

  private static final String ADDRESS_INDEX = "address/address-index";
  private static final String UPDATE_ADDRESS = "address/update-address";
  private static final String ADD_ADDRESS = "address/add-address";
  private static final String DELETE_ADDRESS = "address/delete-address";

  @GetMapping("/{companyCode}")
  public String getAll(@PathVariable("companyCode") String companyCode,
                       Model model) throws Exception {
    Optional<Company> company = companyRepository.findById(companyCode);

    if (company.isEmpty()) {
      throw new Exception("Invalid Company Code " + companyCode);
    } else {
      model.addAttribute("companyCode", companyCode);
      List<Address> addresses =
          addressRepository.findByCompany(company.get());

      if (addresses.isEmpty()) {
        model.addAttribute("addressList", "empty");
      } else {
        model.addAttribute("addressList", addresses);
      }
    }
    return ADDRESS_INDEX;
  }

  @GetMapping("/add/{companyCode}")
  public String add(@PathVariable("companyCode") String companyCode,
                    Model model) {
    Company existingCompany =
        companyRepository.findById(companyCode)
            .orElseThrow(() -> new IllegalArgumentException("Invalid " +
                "Company Code" + companyCode));
    Address address = new Address();
    model.addAttribute("companyCode", companyCode);
    model.addAttribute("address", address);
    return ADD_ADDRESS;
  }

  @Transactional
  @PostMapping("/add/{companyCode}")
  public String add(@PathVariable("companyCode") String companyCode,
                    @ModelAttribute @Valid Address address,
                    Errors errors) throws Exception {
    if (errors.hasErrors()) {
      return ADD_ADDRESS;
    }
    Optional<Company> optionalCompany = companyRepository.findById(companyCode);

    if (optionalCompany.isEmpty()) {
      throw new Exception("Invalid Company Code" + companyCode);
    } else {
      Company company = optionalCompany.get();
      List<Address> addresses =
          addressRepository.findByCompany(company);

      // no address in db and current address both are not primary
      if (isNoAddressPrimary(addresses) && (!address.getIsPrimary())) {
        errors.rejectValue("isPrimary", "isPrimary.missing");
        return ADD_ADDRESS;
      }
      // if current address is primary
      if (address.getIsPrimary()) {
        makeExistingPrimaryAsSecondary(addresses);
      }
      address.setCompany(company);
      addressRepository.save(address);
    }
    return "redirect:/addresses/" + companyCode;
  }

  @GetMapping("/update/{companyCode}/{addressKey}")
  public String update(@PathVariable("companyCode") String companyCode,
                       @PathVariable("addressKey") AddressKey addressKey,
                       Model model) {
    Address existingAddress = addressRepository.findById(addressKey)
        .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
            "key:" + addressKey));
    model.addAttribute("address", existingAddress);
    model.addAttribute("companyCode", companyCode);
    return UPDATE_ADDRESS;
  }

  @Transactional
  @PostMapping("/update/{companyCode}/{addressKey}")
  public String update(@PathVariable("companyCode") String companyCode,
                       @PathVariable("addressKey") AddressKey addressKey,
                       @ModelAttribute @Valid Address address,
                       Errors errors) throws Exception {
    if (errors.hasErrors()) {
      return UPDATE_ADDRESS;
    }
    Optional<Company> optionalCompany = companyRepository.findById(companyCode);

    if (optionalCompany.isEmpty()) {
      throw new Exception("Invalid Company Code" + companyCode);
    } else {
      Address existingAddress = addressRepository.findById(addressKey)
          .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
              "key:" + addressKey));
      Company company = optionalCompany.get();
      List<Address> addresses =
          addressRepository.findByCompany(company);

      // no address in db and current address both are not primary
      if (isNoAddressPrimary(addresses) && (!address.getIsPrimary())) {
        errors.rejectValue("isPrimary", "isPrimary.missing");
        return ADD_ADDRESS;
      }
      // if current address is primary and that is the only address
      if (address.getIsPrimary() && (addresses.size() > 1)) {
        makeExistingPrimaryAsSecondary(addresses);
      }
      existingAddress.setIsPrimary(address.getIsPrimary());
      existingAddress.setAddress2(address.getAddress2());
      addressRepository.save(existingAddress);
    }
    return "redirect:/addresses/" + companyCode;
  }

  @Transactional
  @GetMapping("/delete/{companyCode}/{addressKey}")
  public String delete(@PathVariable("companyCode") String companyCode,
                       @PathVariable AddressKey addressKey) throws Exception {
    Address address = addressRepository.findById(addressKey)
        .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
            "key:" + addressKey));

    Optional<Company> optionalCompany = companyRepository.findById(companyCode);

    if (optionalCompany.isEmpty()) {
      throw new Exception("Invalid Company Code" + companyCode);
    } else {
      Company company = optionalCompany.get();

      // get the list of addresses for the curretn company from the db.
      List<Address> addresses = addressRepository.findByCompany(company);
      addresses.remove(address);

      // if there are other addresses in db and none of them is primary
      if (((addresses.size() != 0)) && isNoAddressPrimary(addresses)) {
        // do not delete and display an appropriate message
        return DELETE_ADDRESS;
      }
      addressRepository.delete(address);
    }
    return "redirect:/addresses/" + companyCode;
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
