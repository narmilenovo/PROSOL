package com.example.plantservice.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.plantservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class StorageLocationResponse {
    private Long id;
    private String storageLocationCode;
    private String storageLocationName;
    private Boolean storageLocationStatus;
    private PlantResponse plant;

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;

    private String createdBy;
    private Date createdAt;
    private List<UpdateAuditHistory> updateAuditHistories;
}
