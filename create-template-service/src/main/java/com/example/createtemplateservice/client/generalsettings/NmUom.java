package com.example.createtemplateservice.client.generalsettings;

import lombok.Data;

import java.util.Date;

@Data
public class NmUom {
    private Long id;
    private String name;
    private Boolean status;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
