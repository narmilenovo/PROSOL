package com.example.generalservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialTypeRequest {
    private String materialCode;
    private String materialName;
    private Boolean materialStatus;
}
