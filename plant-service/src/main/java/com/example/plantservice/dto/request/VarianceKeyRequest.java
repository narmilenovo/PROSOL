package com.example.plantservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VarianceKeyRequest {
    private String varianceKeyCode;
    private String varianceKeyName;
    private Boolean varianceKeyStatus;
}
