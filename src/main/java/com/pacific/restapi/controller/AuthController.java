package com.pacific.restapi.controller;

import com.pacific.restapi.annotations.ApiController;
import com.pacific.restapi.annotations.DataValidation;
import com.pacific.restapi.dto.*;
import com.pacific.restapi.service.AuthService;
import com.pacific.restapi.service.UserService;
import com.pacific.restapi.util.UrlConstants;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@ApiController
@RequestMapping(UrlConstants.AuthManagement.ROOT)
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping(UrlConstants.AuthManagement.LOGIN)
    public Response login(@Validated({LoginRequestDto.CreateValidation.class}) @RequestBody LoginRequestDto loginRequestDto, BindingResult bindingResults, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = authService.login(loginRequestDto);
        httpServletResponse.setStatus(response.getStatusCode());
        return response;
    }

    @PostMapping(UrlConstants.UserManagement.CREATE)
    @DataValidation
    public Response create(@RequestBody @Valid UserDto userDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.create(userDto);
    }

    /*@PostMapping(UrlConstants.AuthManagement.FORGOT_PASSWORD)
    @DataValidation
    public Response forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto forgotPasswordRequestDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.createForgotPasswordRequest(forgotPasswordRequestDto);
    }*/

    /*@PostMapping(UrlConstants.AuthManagement.CHANGE_PASSWORD)
    @DataValidation
    public Response changePassword(@RequestBody @Valid ForgotPasswordDto forgotPasswordDto, BindingResult bindingResult, HttpServletResponse httpServletResponse, HttpServletRequest request) {
        return userService.changeForgottenPassword(forgotPasswordDto);
    }*/
}
