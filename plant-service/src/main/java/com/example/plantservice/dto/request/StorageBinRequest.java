package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageBinRequest {
    private String code;
    private String title;
    private Boolean status;
    private Long plantId;
    private Long storageLocationId;
}
