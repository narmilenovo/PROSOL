package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderUnitResponse {
    private Long id;
    private String ouCode;
    private String ouName;
    private Boolean ouStatus;
}

