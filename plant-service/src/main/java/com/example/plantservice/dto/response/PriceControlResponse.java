package com.example.plantservice.dto.response;


import lombok.Data;

@Data
public class PriceControlResponse {
    private Long id;
    private String priceControlCode;
    private String priceControlName;
    private Boolean priceControlStatus;
}
