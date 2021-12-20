package com.pacific.restapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class BankDto {
    private Long id;
    private String name;
    @JsonIgnore
    private List<BranchDto> branches;
}
