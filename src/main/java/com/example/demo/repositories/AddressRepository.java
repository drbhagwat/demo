package com.example.demo.repositories;

import com.example.demo.models.Address;
import com.example.demo.models.AddressKey;
import com.example.demo.models.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, AddressKey> {
  Page<Address> findAll(Pageable pageable);
  List<Address> findByCompany(Company company);
}
