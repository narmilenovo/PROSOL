package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class ValuationMaterialResponse {
    private Long id;
    private String valuationClassCode;
    private String valuationClassName;
    private MaterialTypeResponse material;
    private Boolean valuationClassStatus;
}
