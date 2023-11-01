package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesUnitRequest {
    private String salesCode;
    private String salesName;
    private Boolean salesStatus;
}
