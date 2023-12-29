package com.example.requestitemservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestItemRequest {
    private Long plantId;
    private Long storageLocationId;
    private Long materialTypeId;
    private Long industrySectorId;
    private Long materialGroupId;
    private String source;
    private String attachment;
}
