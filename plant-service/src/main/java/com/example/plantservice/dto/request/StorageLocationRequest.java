package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocationRequest {
    private String storageLocationCode;
    private String storageLocationTitle;
    private Boolean status;
    private String plantId;
}
