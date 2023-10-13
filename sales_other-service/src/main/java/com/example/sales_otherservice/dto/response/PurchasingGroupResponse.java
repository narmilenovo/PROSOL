package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasingGroupResponse {
    private Long id;
    private String pgCode;
    private String pgName;
    private Boolean pgStatus;
}