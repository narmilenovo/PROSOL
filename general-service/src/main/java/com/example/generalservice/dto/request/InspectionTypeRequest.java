package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionTypeRequest {
    @Schema(description = "Inspection Type Request", example = "ITR-001")
    private String inTypeCode;

    @Schema(description = "Inspection Type Name")
    private String inTypeName;

    @Schema(description = "Inspection Type Status", example = "true")
    private Boolean inTypeStatus;

}
