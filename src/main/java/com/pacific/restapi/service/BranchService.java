package com.pacific.restapi.service;

import com.pacific.restapi.dto.BranchDto;
import com.pacific.restapi.dto.Response;
import org.springframework.data.domain.Pageable;

public interface BranchService {
    Response create(BranchDto branchDto);

    Response update(Long id, BranchDto branchDto);

    Response delete(Long id);

    Response get(Long id);

    Response getAll(Pageable pageable, boolean isExport, String search, String status);

    Response getAllByBank(Long bankId);

    Response getByNameAndBank(String name, Long bankId);
}
