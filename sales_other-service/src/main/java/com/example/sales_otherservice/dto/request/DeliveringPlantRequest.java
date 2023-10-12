package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveringPlantRequest {
    private String dpCode;
    private String dpName;
    private Boolean dpStatus;
    private Long plantId;
}
