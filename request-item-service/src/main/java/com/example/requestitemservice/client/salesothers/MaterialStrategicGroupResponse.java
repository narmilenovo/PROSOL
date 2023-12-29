package com.example.requestitemservice.client.salesothers;

import java.util.Date;

import lombok.Data;

@Data
public class MaterialStrategicGroupResponse {
    private Long id;
    private String msCode;
    private String msName;
    private Boolean msStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
