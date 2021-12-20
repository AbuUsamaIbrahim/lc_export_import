package com.pacific.restapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


@Data
public class BranchDto {
    private Long id;
    private String name;
    private String address;
    private BankDto bank;
}
