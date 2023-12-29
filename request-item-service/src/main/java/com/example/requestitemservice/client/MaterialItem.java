package com.example.requestitemservice.client;

import java.util.Date;

import com.example.requestitemservice.client.general.IndustrySectorResponse;
import com.example.requestitemservice.client.general.MaterialTypeResponse;
import com.example.requestitemservice.client.plant.PlantResponse;
import com.example.requestitemservice.client.plant.StorageLocationResponse;
import com.example.requestitemservice.client.salesothers.MaterialStrategicGroupResponse;

import lombok.Data;

@Data
public class MaterialItem {
    private Long id;
    private PlantResponse plant;
    private StorageLocationResponse storageLocation;
    private MaterialTypeResponse materialType;
    private IndustrySectorResponse industrySector;
    private MaterialStrategicGroupResponse materialGroup;
    private String source;
    private String attachment;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
