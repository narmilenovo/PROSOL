package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxClassificationTypeResponse {
    private Long id;
    private String tctCode;
    private String tctName;
    private Boolean tctStatus;
}