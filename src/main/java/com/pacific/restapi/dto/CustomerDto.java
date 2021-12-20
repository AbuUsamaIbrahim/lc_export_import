package com.pacific.restapi.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class CustomerDto {
    private Long id;
    @NotBlank(message = "Name can't be blank")
    @Size(min = 6, max = 70, message = "Name Length must be within 6 to 70 characters")
    private String name;
    private String address;
    @Pattern(regexp = "^[0][1][0-9]{9}$", message = "Mobile Number must be start with 01 and followed by other digits")
    @Size(min = 11, max = 11, message = "Mobile No must be 11 digit")
    private String mobileNo;
}
