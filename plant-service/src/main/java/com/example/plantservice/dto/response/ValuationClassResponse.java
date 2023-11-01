package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class ValuationClassResponse {
    private Long id;
    private String valuationClassCode;
    private String valuationClassName;
    private Long materialTypeId;
    private Boolean valuationClassStatus;
}
