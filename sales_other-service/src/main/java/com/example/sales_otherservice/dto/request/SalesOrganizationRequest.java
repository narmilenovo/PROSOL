package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrganizationRequest {
    @Schema(description = "Sales Organization Code", example = "SO001")
    private String soCode;

    @Schema(description = "Sales Organization Name")
    private String soName;

    @Schema(description = "Sales Organization Status", example = "true")
    private Boolean soStatus;
}
