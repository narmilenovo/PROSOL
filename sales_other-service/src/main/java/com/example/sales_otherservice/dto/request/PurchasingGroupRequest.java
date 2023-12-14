package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasingGroupRequest {

    @Schema(description = "Purchasing Group Code", example = "PG001")
    private String pgCode;

    @Schema(description = "Purchasing Group Name", example = "Purchasing Group Name")
    private String pgName;
    
    @Schema(description = "Purchasing Group Status", example = "true")
    private Boolean pgStatus;
}
