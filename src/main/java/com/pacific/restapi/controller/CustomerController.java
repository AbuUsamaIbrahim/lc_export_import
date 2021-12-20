package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.CustomerDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.service.CustomerService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.CustomerManagement.ROOT)
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(UrlConstants.CustomerManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid CustomerDto customerDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return customerService.create(customerDto);
    }

    @PutMapping(UrlConstants.CustomerManagement.UPDATE)
    @DataValidation
    public Response update(@PathVariable("id") Long id, @RequestBody @Valid CustomerDto customerDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return customerService.update(id, customerDto);
    }

    @DeleteMapping(UrlConstants.CustomerManagement.DELETE)
    public Response delete(@PathVariable("id") Long id, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return customerService.delete(id);
    }

    @GetMapping(UrlConstants.CustomerManagement.GET)
    public Response get(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        return customerService.get(id);
    }

    @GetMapping(UrlConstants.CustomerManagement.GET_BY_NAME)
    public Response getCustomerName(@RequestParam("name") String name, @RequestParam("address") String address, HttpServletResponse httpServletResponse) {
        return customerService.getName(name, address);
    }

    @GetMapping(UrlConstants.CustomerManagement.GET_ALL)
    public Response getAll(HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return customerService.getAll(pageable, isExport, search, status);
    }
}
