package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DivisionRequest {

    @Schema(description = "Division Code", example = "Div-001")
    private String divCode;

    @Schema(description = "Division Name")
    private String divName;

    @Schema(description = "Division Status", example = "true")
    private Boolean divStatus;
}
