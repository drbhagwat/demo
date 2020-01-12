/*
package com.example.demo.controllers;

import com.example.demo.models.Address;
import com.example.demo.models.Company;
import com.example.demo.repositories.CompanyRepository;
import com.example.demo.services.AddressService;

import com.example.demo.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AddressController {
  @Autowired
  private AddressService addressService;

  @Autowired
  private AddressRepository AddressRepository;

  @Autowired
  private CompanyRepository companyRepository;

  @RequestMapping("/{entity}/{entityId}/addresses/getAll")
  public String getAll(Model model, @PathVariable("entity") String entity, @PathVariable("entityId") String entityId, @RequestParam(defaultValue = "0") Integer pageNo,
                       @RequestParam(defaultValue = "1") Integer pageSize) {
    List<Address> addresses = AddressRepository.findAll();
    model.addAttribute("entity", entity);
    model.addAttribute("entityId", entityId);

    if (addresses.size() > 0) {
      model.addAttribute("data", AddressRepository.findAll(PageRequest.of(pageNo, pageSize)));
      model.addAttribute("currentPage", pageNo);
    }
    return "address";
  }

  @PostMapping(value = "/add")
  public String add(Address address) {
    addressService.createOrUpdateAddress(address);
    return "redirect:/companies/getAll";
  }

  @RequestMapping(path = {"/{entity}/{entityId}/addresses/edit", "/{entity}/{entityId}/addresses/edit/{id}"})
  @ResponseBody
  public String edit(Model model, @PathVariable("entity") String entity, @PathVariable("entityId") String entityId, @PathVariable("id") Optional<Integer> id) {
    if (id.isPresent()) {
      Optional<Address> address = addressService.get(id);
      model.addAttribute("address", address);
      addressService.save(address.get());
    } else {
      model.addAttribute("address", new Address());
    }
    return "redirect:/{entity}/{entityId}/addresses/getAll";
  }

  @GetMapping("value = /{entity}/{entityId}/addresses/delete")
  public String delete(@PathVariable("entity") String entity, @PathVariable("entityId") String entityId, int id) {
    addressService.delete(id);
    return "redirect:/{entity}/{entityId}/addresses/getAll";
  }
}
*/
