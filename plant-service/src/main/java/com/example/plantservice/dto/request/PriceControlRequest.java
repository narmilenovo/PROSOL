package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceControlRequest {
    private String priceControlCode;
    private String priceControlName;
    private Boolean priceControlStatus;
}
