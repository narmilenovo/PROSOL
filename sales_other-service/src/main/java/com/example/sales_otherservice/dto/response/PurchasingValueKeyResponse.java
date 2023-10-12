package com.example.sales_otherservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasingValueKeyResponse {
    private Long id;
    private String pvkCode;
    private String pvkName;
    private Boolean pvkStatus;
}
