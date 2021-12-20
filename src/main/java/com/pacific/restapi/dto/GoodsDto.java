package com.pacific.restapi.dto;

import lombok.Data;

@Data
public class GoodsDto {
    private Long id;
    private String description;
    private Integer quantity;
    private String remarks;
    private Double unitPrice;
    private String unit;
    private Integer packOrMarks;
}
