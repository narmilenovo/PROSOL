package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccAssignmentRequest {
    private String accCode;
    private String accName;
    private Boolean accStatus;
}
