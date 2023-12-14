package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MaterialTypeRequest {

    @Schema(description = "Material code", example = "M-001")
    private String materialCode;

    @Schema(description = "Material name", example = "Material 1")
    private String materialName;

    @Schema(description = "Material status")
    private Boolean materialStatus;
}
