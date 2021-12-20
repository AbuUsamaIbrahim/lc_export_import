package com.pacific.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity(name = "branch")
public class Branch extends BaseModel {

    private String name;
    private String address;
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Bank bank;
}
