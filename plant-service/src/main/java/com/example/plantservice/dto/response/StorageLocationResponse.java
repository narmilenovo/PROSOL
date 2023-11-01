package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class StorageLocationResponse {
    private Long id;
    private String storageLocationCode;
    private String storageLocationTitle;
    private Boolean status;
}
