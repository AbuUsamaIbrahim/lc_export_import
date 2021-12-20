package com.pacific.restapi.service;

import com.pacific.restapi.dto.Response;
import com.pacific.restapi.dto.SaleDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;

import javax.servlet.http.HttpServletResponse;

public interface SaleService {
    HttpEntity<byte[]> getAllReportsByType(Long id, String type, HttpServletResponse response);

    Response create(SaleDto saleDto);

    Response update(Long id, SaleDto saleDto);

    Response delete(Long id);

    Response get(Long id);


    Response getAll(Pageable pageable, boolean isExport, String search, String status);

    Response getbyPerformaInvoice(String performaInvoice);

    HttpEntity<byte[]> getReportsByParams(Long sales_id, String bank, String branch, String address, String account, HttpServletResponse response);
}
