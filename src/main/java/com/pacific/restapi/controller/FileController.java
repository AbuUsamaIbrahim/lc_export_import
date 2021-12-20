package com.pacific.restapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.service.SaleService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author grawat
 */
@ApiController
@RequestMapping(value = UrlConstants.FileManagement.ROOT)
public class FileController {
    private final SaleService saleService;

    public FileController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping(UrlConstants.FileManagement.DOWNLOAD_FILE)
    public HttpEntity<byte[]> download(@RequestParam(name = "sales_id") Long sales_id, @RequestParam(name = "report_type") String report_type, HttpServletResponse response, HttpServletRequest request) {
        return saleService.getAllReportsByType(sales_id, report_type, response);
    }

    @GetMapping(UrlConstants.FileManagement.DOWNLOAD_FILE_BY_PARAMS)
    public HttpEntity<byte[]> downloadByParams(@RequestParam(name = "sales_id") Long sales_id, @RequestParam(name = "bank") String bank, @RequestParam(name = "branch") String branch, @RequestParam(name = "address") String address, @RequestParam(name = "account") String account, HttpServletResponse response, HttpServletRequest request) {
        return saleService.getReportsByParams(sales_id, bank, branch, address, account, response);
    }
}