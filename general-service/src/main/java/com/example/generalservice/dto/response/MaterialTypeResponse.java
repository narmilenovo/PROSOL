package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialTypeResponse {
    private Long id;
    private String materialCode;
    private String materialName;
    private Boolean materialStatus;
}
