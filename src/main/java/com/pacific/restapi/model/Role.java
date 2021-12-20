package com.pacific.restapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "role")
@Data
public class Role extends BaseModel {


    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users = new ArrayList<>();
    //    private BigInteger priority;
    private String description;
    private String name;
}