package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxClassificationTypeRequest {

    @Schema(description = "Tax Classification Type Code", example = "TCT001")
    private String tctCode;

    @Schema(description = "Tax Classification Type Name", example = "GST")
    private String tctName;

    @Schema(description = "Tax Classification Type Status", example = "true", allowableValues = "true,false")
    private Boolean tctStatus;
}
