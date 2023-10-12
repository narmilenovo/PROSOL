package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrganizationResponse {
    private Long id;
    private String soCode;
    private String soName;
    private Boolean soStatus;
}
