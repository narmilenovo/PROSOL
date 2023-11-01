package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitCenterRequest {
    private String profitCenterCode;
    private String profitCenterTitle;
    private Boolean status;
    private Long plantId;
}
