package com.pacific.restapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {

    private Long id;
    //    private List<UserDto> users;
    private Long priority;
    private String description;
    private String name;
}
