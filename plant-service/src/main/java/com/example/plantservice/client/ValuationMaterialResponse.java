package com.example.plantservice.client;

import java.util.Date;
import java.util.Map;

import com.example.plantservice.client.General.MaterialTypeResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ValuationMaterialResponse {
    private Long id;
    private String valuationClassCode;
    private String valuationClassName;
    private MaterialTypeResponse material;
    private Boolean valuationClassStatus;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

    @JsonAnyGetter
    @JsonIgnore
    private Map<String, Object> dynamicFields;
}
