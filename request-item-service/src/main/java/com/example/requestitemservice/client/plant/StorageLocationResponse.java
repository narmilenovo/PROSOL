package com.example.requestitemservice.client.plant;

import java.util.Date;

import lombok.Data;

@Data
public class StorageLocationResponse {
    private Long id;
    private String storageLocationCode;
    private String storageLocationTitle;
    private Boolean status;
    private PlantResponse plant;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
