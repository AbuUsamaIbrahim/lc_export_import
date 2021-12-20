package com.pacific.restapi.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequestDto {
    @NotBlank(message = "Email can't be blank")
    @Email(message = "Invalid Email")
    private String email;
}
