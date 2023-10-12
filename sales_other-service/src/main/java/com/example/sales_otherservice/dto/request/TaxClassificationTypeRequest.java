package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxClassificationTypeRequest {
    private String tctCode;
    private String tctName;
    private Boolean tctStatus;
}
