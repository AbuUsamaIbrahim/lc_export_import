package com.pacific.restapi.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.List;

@Entity(name = "user")
@Data
public class User extends BaseModel {


    private String name;
    private String username;
    @NaturalId
    private String email;
    private String password;
    @NaturalId
    private String nid;
    private String status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private List<Role> roles;
}