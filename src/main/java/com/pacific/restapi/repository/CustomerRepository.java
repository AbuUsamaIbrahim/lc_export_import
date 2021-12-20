package com.pacific.restapi.repository;

import com.pacific.restapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByIdAndIsActiveTrue(Long id);

    Customer findByNameAndAddressAndIsActiveTrue(String name, String address);

    List<Customer> findAllByIsActiveTrue();
}
