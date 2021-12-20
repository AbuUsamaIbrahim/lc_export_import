package com.pacific.restapi.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "sales_price_history")
public class SalesPriceHistory extends BaseModel {

    @Column(name = "sales_id", updatable = false)
    private Long salesId;
    @Column(name = "goods_id", updatable = false)
    private Long goodsId;
    @Column(name = "total_quantity", updatable = false)
    private Integer totalQuantity;
    @Column(name = "unit_price", updatable = false)
    private Double unitPrice;
    @Column(updatable = false)
    private String unit;
    @Column(name = "pack_or_marks", updatable = false)
    private Integer packOrMarks;
    @Column(name = "goods_desc", updatable = false)
    private String goodsDesc;

}
