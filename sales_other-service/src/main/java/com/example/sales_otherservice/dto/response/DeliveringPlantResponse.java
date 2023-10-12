package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveringPlantResponse {
    private Long id;
    private String dpCode;
    private String dpName;
    private Boolean dpStatus;
    private Long plantId;
}
