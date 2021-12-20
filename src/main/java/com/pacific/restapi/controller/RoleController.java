package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.Response;
import com.pacific.restapi.dto.RoleDto;
import com.pacific.restapi.dto.UserDto;
import com.pacific.restapi.service.RoleService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@ApiController
@RequestMapping(UrlConstants.RoleManagement.ROOT)
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(UrlConstants.RoleManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid RoleDto roleDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return roleService.create(roleDto);
    }

    @PutMapping(UrlConstants.RoleManagement.ASSIGN_USERS)
    public Response assignUsers(@PathVariable("id") Long id, @RequestBody List<UserDto> userDtoList, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return roleService.assignUsers(id, userDtoList);
    }

    @PutMapping(UrlConstants.RoleManagement.UPDATE)
    @DataValidation
    public Response update(@PathVariable("id") Long id, @RequestBody @Valid RoleDto roleDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return roleService.update(id, roleDto);
    }

    @DeleteMapping(UrlConstants.RoleManagement.DELETE)
    public Response delete(@PathVariable("id") Long id, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return roleService.delete(id);
    }

    @GetMapping(UrlConstants.RoleManagement.GET)
    public Response get(@PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        return roleService.get(id);
    }

    @GetMapping(UrlConstants.RoleManagement.GET_ALL)
    public Response getAll(HttpServletResponse httpServletResponse, Pageable pageable,
                           @RequestParam(value = "export", defaultValue = "false") boolean isExport,
                           @RequestParam(value = "search", defaultValue = "") String search,
                           @RequestParam(value = "status", defaultValue = "") String status) {

        return roleService.getAll(pageable, isExport, search, status);
    }
}
