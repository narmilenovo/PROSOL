package com.example.sales_otherservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasingValueKeyRequest {
    private String pvkCode;
    private String pvkName;
    private Boolean pvkStatus;
}
