package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuationClassRequest {
    private String valuationClassCode;
    private String valuationClassName;
    private Long materialTypeId;
    private Boolean valuationClassStatus;
}
