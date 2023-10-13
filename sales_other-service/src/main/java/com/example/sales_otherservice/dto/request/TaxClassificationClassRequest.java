package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxClassificationClassRequest {
    private String tccCode;
    private String tccName;
    private Boolean tccStatus;
}