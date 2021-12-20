package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.BankDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.service.BankService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.BankManagement.ROOT)
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping(UrlConstants.BankManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid BankDto bankDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return bankService.create(bankDto);
    }

    @PutMapping(UrlConstants.BankManagement.UPDATE)
    @DataValidation
    public Response update(@PathVariable("id") Long id, @RequestBody @Valid BankDto bankDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return bankService.update(id, bankDto);
    }

    @DeleteMapping(UrlConstants.BankManagement.DELETE)
    public Response delete(@PathVariable("id") Long id, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return bankService.delete(id);
    }

    @GetMapping(UrlConstants.BankManagement.GET)
    public Response get(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        return bankService.get(id);
    }

    @GetMapping(UrlConstants.BankManagement.GET_BY_NAME)
    public Response getBankName(@RequestParam("name") String name, HttpServletResponse httpServletResponse) {
        return bankService.getName(name);
    }

    @GetMapping(UrlConstants.BankManagement.GET_ALL)
    public Response getAll(HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return bankService.getAll(pageable, isExport, search, status);
    }

    @GetMapping(UrlConstants.BankManagement.GET_BANK_BRANCH)
    public Response getBankBranch(HttpServletResponse httpServletResponse) {
        return bankService.getAllBankBranch();
    }
}
