package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxClassificationClassResponse {
    private Long id;
    private String tccCode;
    private String tccName;
    private Boolean tccStatus;
}
