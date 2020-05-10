package com.example.demo.repositories;

import com.example.demo.models.Address;
import com.example.demo.models.AddressKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, AddressKey> {
  Page<Address> findAll(Pageable pageable);
}
