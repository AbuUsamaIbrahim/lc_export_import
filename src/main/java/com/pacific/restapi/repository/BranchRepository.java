package com.pacific.restapi.repository;

import com.pacific.restapi.model.Bank;
import com.pacific.restapi.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Branch findByIdAndIsActiveTrue(Long id);

    Branch findByNameAndBankAndIsActiveTrue(String name, Bank bankId);

    List<Branch> findAllByBankAndIsActiveTrue(Bank bank);

    List<Branch> findAllByIsActiveTrue();
}
