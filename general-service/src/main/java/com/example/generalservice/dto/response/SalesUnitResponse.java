package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesUnitResponse {
    private Long id;
    private String salesCode;
    private String salesName;
    private Boolean salesStatus;
}
