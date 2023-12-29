package com.example.requestitemservice.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestItemResponse {
    private Long id;
    private Long plantId;
    private Long storageLocationId;
    private Long materialTypeId;
    private Long industrySectorId;
    private Long materialGroupId;
    private String source;
    private String attachment;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
