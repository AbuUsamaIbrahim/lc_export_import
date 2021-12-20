package com.pacific.restapi.service;

import com.pacific.restapi.dto.BankDto;
import com.pacific.restapi.dto.Response;
import org.springframework.data.domain.Pageable;

public interface BankService {
    Response create(BankDto bankDto);

    Response update(Long id, BankDto bankDto);

    Response delete(Long id);

    Response get(Long id);

    Response getAll(Pageable pageable, boolean isExport, String search, String status);

    Response getAllBankBranch();

    Response getName(String name);
}
