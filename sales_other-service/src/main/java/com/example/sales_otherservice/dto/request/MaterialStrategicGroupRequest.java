package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialStrategicGroupRequest {
    @Schema(description = "Material Strategic Group Code", example = "MS001")
    private String msCode;

    @Schema(description = "Material Strategic Group Name", example = "Material Strategic Group 1")
    private String msName;

    @Schema(description = "Material Strategic Group Status", example = "true")
    private Boolean msStatus;
}
