package com.pacific.restapi.dto;

import lombok.Data;

@Data
public class BankBranchDto {
    private Long id;
    private String bankName;
    private String branchName;
    private String bankBranchName;
}
