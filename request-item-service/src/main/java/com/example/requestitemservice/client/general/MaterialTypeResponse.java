package com.example.requestitemservice.client.general;

import java.util.Date;

import lombok.Data;

@Data
public class MaterialTypeResponse {
    private Long id;
    private String materialCode;
    private String materialName;
    private Boolean materialStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
