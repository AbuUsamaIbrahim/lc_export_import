package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.BranchDto;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.service.BranchService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.BranchManagement.ROOT)
public class BranchController {
    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping(UrlConstants.BranchManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid BranchDto branchDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return branchService.create(branchDto);
    }

    @PutMapping(UrlConstants.BranchManagement.UPDATE)
    @DataValidation
    public Response update(@PathVariable("id") Long id, @RequestBody @Valid BranchDto branchDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return branchService.update(id, branchDto);
    }

    @DeleteMapping(UrlConstants.BranchManagement.DELETE)
    public Response delete(@PathVariable("id") Long id, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return branchService.delete(id);
    }

    @GetMapping(UrlConstants.BranchManagement.GET)
    public Response get(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        return branchService.get(id);
    }

    @GetMapping(UrlConstants.BranchManagement.GET_ALL_BY_BANK)
    public Response getAllByBank(@RequestParam("bankId") Long bankId, HttpServletResponse httpServletResponse) {
        return branchService.getAllByBank(bankId);
    }

    @GetMapping(UrlConstants.BranchManagement.GET_BY_NAME_AND_BANK)
    public Response getAllByBank(@RequestParam("name") String name, @RequestParam("bankId") Long bankId, HttpServletResponse httpServletResponse) {
        return branchService.getByNameAndBank(name, bankId);
    }

    @GetMapping(UrlConstants.BranchManagement.GET_ALL)
    public Response getAll(HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return branchService.getAll(pageable, isExport, search, status);
    }
}
