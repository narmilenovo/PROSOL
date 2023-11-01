package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValuationCategoryRequest {
    private String valuationCategoryCode;
    private String valuationCategoryName;
    private Boolean valuationCategoryStatus;
}
