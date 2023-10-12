package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccAssignmentResponse {
    private Long id;
    private String accCode;
    private String accName;
    private Boolean accStatus;
}
