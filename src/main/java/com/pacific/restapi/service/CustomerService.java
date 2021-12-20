package com.pacific.restapi.service;

import com.pacific.restapi.dto.CustomerDto;
import com.pacific.restapi.dto.Response;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Response create(CustomerDto customerDto);

    Response update(Long id, CustomerDto customerDto);

    Response delete(Long id);

    Response get(Long id);

    Response getAll(Pageable pageable, boolean isExport, String search, String status);

    Response getName(String name, String address);
}
