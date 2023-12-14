package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccAssignmentRequest {

    @Schema(description = "Account Assignment Code", example = "ACC001")
    private String accCode;

    @Schema(description = "Account Assignment Name", example = "Account Assignment Name")
    private String accName;

    @Schema(description = "Account Assignment Status", example = "true")
    private Boolean accStatus;
}
