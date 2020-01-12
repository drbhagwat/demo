/*
package com.example.demo.services;

import com.example.demo.models.Address;
import com.example.demo.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressService {
  @Autowired
  private AddressRepository addressRepository;

  public Optional<Address> get(Optional<Integer> id) {
    return addressRepository.findById(id.get());
  }

  public Address createOrUpdateAddress(Address address) {
    if (address.getId() == 0) {
      return addressRepository.save(address);
    } else {
      Optional<Address> optionalAddress = addressRepository.findById(address.getId());

      if (optionalAddress.isPresent()) {
        Address newAddress = optionalAddress.get();
        newAddress.setAddress1(address.getAddress1());
        newAddress.setAddress2(address.getAddress2());
        newAddress.setCity(address.getCity());
        newAddress.setState(address.getState());
        newAddress.setZip(address.getZip());
        return addressRepository.save(newAddress);
      } else {
        return addressRepository.save(address);
      }
    }
  }

  public void delete(int id) {
    AddressRepository.deleteById(id);
  }
}
*/
