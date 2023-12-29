package com.example.requestitemservice.client.plant;

import java.util.Date;

import lombok.Data;

@Data
public class PlantResponse {
    private Long id;
    private String plantCode;
    private String plantName;
    private Boolean status;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
