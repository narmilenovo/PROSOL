package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class ProfitCenterResponse {
    private Long id;
    private String profitCenterCode;
    private String profitCenterTitle;
    private Boolean status;
}
