package com.pacific.restapi.model;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "action_log")
@Data
public class ActionLog extends BaseModel {

    //    private BigInteger id;
    private String email;
    private String description;
    private String action;
    @Column(name = "row_id")
    private Long rowId;

    public ActionLog() {

    }

    public ActionLog(String email, String description, String action, Long rowId) {
        super();
        this.email = email;
        this.description = description;
        this.action = action;
        this.rowId = rowId;
    }

}
