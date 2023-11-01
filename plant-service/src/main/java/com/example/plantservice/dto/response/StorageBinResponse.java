package com.example.plantservice.dto.response;

import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageLocation;
import lombok.Data;

@Data
public class StorageBinResponse {
    private Long id;
    private String code;
    private String title;
    private Boolean status;
    private Plant plant;
    private StorageLocation storageLocation;
}
