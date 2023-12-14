package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxClassificationClassRequest {
    @Schema(description = "TaxClassificationClass Code", example = "TCC001")
    private String tccCode;

    @Schema(description = "TaxClassificationClass Name")
    private String tccName;

    @Schema(description = "TaxClassificationClass Status", example = "true", allowableValues = "true,false")
    private Boolean tccStatus;
}
