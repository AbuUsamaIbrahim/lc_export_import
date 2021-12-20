package com.pacific.restapi.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private String employeeId;
    private String phoneNo;
    private String username;
    private String password;
    private List<RoleDto> roles;
}
