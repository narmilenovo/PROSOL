package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class VarianceKeyResponse {
    private Long id;
    private String varianceKeyCode;
    private String varianceKeyName;
    private Boolean varianceKeyStatus;
}
