package com.pacific.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity(name = "goods")
@Data
public class Goods extends BaseModel {

    private String description;
    private Integer quantity;
    private String remarks;
    @Column(name = "unit_price")
    private Double unitPrice;
    private String unit;
    @Column(name = "pack_or_marks")
    private Integer packOrMarks;
    @ManyToMany(mappedBy = "goodsList")
    @JsonIgnore
    private List<Sale> sales;
}
