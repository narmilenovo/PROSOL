package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class ValuationCategoryResponse {
    private Long id;
    private String valuationCategoryCode;
    private String valuationCategoryName;
    private Boolean valuationCategoryStatus;
}
