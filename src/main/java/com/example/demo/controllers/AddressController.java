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
  private static final String ADDRESSES = "redirect:/addresses/";
  private static final String IS_PRIMARY = "isPrimary";
  private static final String COMPANY_CODE = "companyCode";

  @GetMapping()
  public Address get(AddressKey addressKey) {
    Optional<Address> optionalAddress = addressRepository.findById(addressKey);

    if (optionalAddress.isEmpty()) {
      return null;
    }
    return optionalAddress.get();
  }

  @GetMapping("/{companyCode}")
  public String getAll(@PathVariable(COMPANY_CODE) String companyCode,
                       Model model) throws Exception {
    Optional<Company> company = companyRepository.findById(companyCode);

    if (company.isEmpty()) {
      throw new Exception("Invalid Company Code " + companyCode);
    } else {
      model.addAttribute(COMPANY_CODE, companyCode);
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
  public String add(@PathVariable(COMPANY_CODE) String companyCode,
                    Model model) {
    companyRepository.findById(companyCode)
            .orElseThrow(() -> new IllegalArgumentException("Invalid " +
                "Company Code" + companyCode));
    Address address = new Address();
    model.addAttribute(COMPANY_CODE, companyCode);
    model.addAttribute("address", address);
    return ADD_ADDRESS;
  }

  @Transactional
  @PostMapping("/add/{companyCode}")
  public String add(@PathVariable(COMPANY_CODE) String companyCode,
                    @ModelAttribute("address") @Valid Address address,
                    Errors errors) {
    if (errors.hasErrors()) {
      return ADD_ADDRESS;
    }
    Company company = companyRepository.getOne(companyCode);
    List<Address> addressesinCompany =
        addressRepository.findByCompany(company);
    Address existingAddress = get(address.getAddressKey());
    boolean isCurrentAddressPrimary = address.getIsPrimary();

    if (existingAddress == null) { // adding a new address

      if (addressesinCompany.isEmpty()) {
        // there are no other addresses in the db

        if (!isCurrentAddressPrimary) {
          errors.rejectValue(IS_PRIMARY, "isPrimary.missing");
          return ADD_ADDRESS;
        }
      } else { // there are other addresses in the db
        if (isCurrentAddressPrimary) {
          // if the current address becomes the primary,
          // the existing primary becomes secondary.
          makePrimaryAsSecondary(addressesinCompany);
        }
      }
    } else { // this address already exists
      if (addressesinCompany.size() == 1) {
        // this is the only address

        if (!isCurrentAddressPrimary) {
          errors.rejectValue(IS_PRIMARY, "isPrimary.missing");
          return ADD_ADDRESS;
        }
      } else {
        // there are other addresses

        if (isCurrentAddressPrimary) {
          // if the current address becomes the primary, make the
          // existing primary address secondary.
          makePrimaryAsSecondary(addressesinCompany);
        }
      }
    }
    address.setCompany(company);
    addressRepository.save(address);
    return ADDRESSES + companyCode;
  }

  @GetMapping("/update/{companyCode}/{addressKey}")
  public String update(@PathVariable(COMPANY_CODE) String companyCode,
                       @PathVariable("addressKey") AddressKey addressKey,
                       Model model) {
    companyRepository.findById(companyCode)
        .orElseThrow(() -> new IllegalArgumentException("Invalid " +
            "Company Code" + companyCode));
    Address existingAddress = addressRepository.findById(addressKey)
        .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
            "key:" + addressKey));
    model.addAttribute("address", existingAddress);
    model.addAttribute(COMPANY_CODE, companyCode);
    return UPDATE_ADDRESS;
  }

  @Transactional
  @PostMapping("/update/{companyCode}/{addressKey}")
  public String update(@PathVariable(COMPANY_CODE) String companyCode,
                       @PathVariable("addressKey") AddressKey addressKey,
                       @ModelAttribute @Valid Address address,
                       Errors errors) {
    if (errors.hasErrors()) {
      return UPDATE_ADDRESS;
    }
    Company company = companyRepository.findById(companyCode).get();
    List<Address> addressesinCompany =
        addressRepository.findByCompany(company);
    Address existingAddress = get(address.getAddressKey());
    boolean isCurrentAddressPrimary = address.getIsPrimary();

    if (addressesinCompany.size() == 1) {
      // this is the only address

      if (!isCurrentAddressPrimary) {
        errors.rejectValue(IS_PRIMARY, "isPrimary.missing");
        return UPDATE_ADDRESS;
      }
    } else {
      // there are other addresses

      if (isCurrentAddressPrimary) {
        // if the current address becomes the primary, make the
        // existing primary address secondary.
        makePrimaryAsSecondary(addressesinCompany);
      }
    }
    existingAddress.setIsPrimary(isCurrentAddressPrimary);
    existingAddress.setAddress2(address.getAddress2());
    addressRepository.save(existingAddress);
    return ADDRESSES + companyCode;
  }

  @Transactional
  @GetMapping("/delete/{companyCode}/{addressKey}")
  public String delete(@PathVariable(COMPANY_CODE) String companyCode,
                       @PathVariable AddressKey addressKey) throws Exception {
    Address address = addressRepository.findById(addressKey)
        .orElseThrow(() -> new IllegalArgumentException("Invalid address " +
            "key:" + addressKey));

    Optional<Company> optionalCompany = companyRepository.findById(companyCode);

    if (optionalCompany.isEmpty()) {
      throw new Exception("Invalid Company Code" + companyCode);
    } else {
      Company company = optionalCompany.get();
      // get the list of addresses for the current company from the db.
      List<Address> addresses = addressRepository.findByCompany(company);
      addresses.remove(address);

      // if there are other addresses in db and none of them is primary
      if (addresses.isEmpty() || isNoAddressPrimary(addresses)) {
        // do not delete and display an appropriate message
        return DELETE_ADDRESS;
      }
    }
    addressRepository.delete(address);
    return ADDRESSES + companyCode;
  }

  private boolean isNoAddressPrimary(List<Address> addresses) {
    int size = addresses.size();
    int totalPrimaryAddresses = 0;

    for (int i = 0; i < size; i++) {

      if (addresses.get(i).getIsPrimary().booleanValue()) {
        totalPrimaryAddresses++;
        break;
      }
    }
    return totalPrimaryAddresses == 0;
  }

  @Transactional
  private void makePrimaryAsSecondary(List<Address> addresses) {
    int size = addresses.size();

    for (int i = 0; i < size; i++) {
      Address address = addresses.get(i);

      // make the only primary address in address book to secondary
      if (address.getIsPrimary().booleanValue()) {
        address.setIsPrimary(false);
        addressRepository.save(address);
        return;
      }
    }
  }
}
