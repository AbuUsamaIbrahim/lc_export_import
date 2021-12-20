package com.pacific.restapi.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity(name = "customer")
@Data
public class Customer extends BaseModel {
    private String name;
    private String address;
    @Column(name = "mobile_no")
    private String mobileNo;
}
