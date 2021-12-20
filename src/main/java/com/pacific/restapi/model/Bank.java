package com.pacific.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "bank")
public class Bank extends BaseModel {
    private String name;
    @OneToMany(mappedBy = "bank")
    @JsonIgnore
    private List<Branch> branches;
}
