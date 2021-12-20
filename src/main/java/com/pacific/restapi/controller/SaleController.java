package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.SaleDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.service.SaleService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.SaleManagement.ROOT)
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }


    @PostMapping(UrlConstants.SaleManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid SaleDto saleDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return saleService.create(saleDto);
    }

    @PutMapping(UrlConstants.SaleManagement.UPDATE)
    @DataValidation
    public Response update(@PathVariable("id") Long id, @RequestBody @Valid SaleDto saleDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return saleService.update(id, saleDto);
    }

    @DeleteMapping(UrlConstants.SaleManagement.DELETE)
    public Response delete(@PathVariable("id") Long id, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return saleService.delete(id);
    }

    @GetMapping(UrlConstants.SaleManagement.GET)
    public Response get(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        return saleService.get(id);
    }

    @GetMapping(UrlConstants.SaleManagement.GET_BY_PERFORMAINVOICE)
    public Response get(@RequestParam("pin") String performaInvoice, HttpServletResponse httpServletResponse) {
        return saleService.getbyPerformaInvoice(performaInvoice);
    }

    @GetMapping(UrlConstants.SaleManagement.GET_ALL)
    public Response getAll(HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return saleService.getAll(pageable, isExport, search, status);
    }
}
