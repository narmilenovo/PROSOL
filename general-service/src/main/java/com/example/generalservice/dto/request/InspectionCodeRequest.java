package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionCodeRequest {

    @Schema(description = "Inspection Code Code", example = "INC-001")
    private String inCodeCode;

    @Schema(description = "Inspection Code Name")
    private String inCodeName;

    @Schema(description = "Inspection Code Status", example = "true")
    private Boolean inCodeStatus;
}
