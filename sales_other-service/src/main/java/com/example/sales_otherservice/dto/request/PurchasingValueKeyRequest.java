package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasingValueKeyRequest {

    @Schema(description = "Purchasing Value Key Code", title = "Purchasing Value Key Code", example = "PVK001")
    private String pvkCode;

    @Schema(description = "Purchasing Value Key Name", example = "Purchasing Value Key Name")
    private String pvkName;

    @Schema(description = "Purchasing Value Key Status", example = "true")
    private Boolean pvkStatus;
}
