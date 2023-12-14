package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SalesUnitRequest {

    @Schema(description = "Sales Code", example = "SALES001")
    private String salesCode;

    @Schema(description = "Sales Name", example = "Sales Unit")
    private String salesName;

    @Schema(description = "Sales Status", example = "true")
    private Boolean salesStatus;
}
