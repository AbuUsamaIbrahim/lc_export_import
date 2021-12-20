package com.pacific.restapi.repository;

import com.pacific.restapi.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findByIdAndIsActiveTrue(Long id);

    List<Bank> findAllByIsActiveTrue();

    Bank findByNameAndIsActiveTrue(String name);
}
