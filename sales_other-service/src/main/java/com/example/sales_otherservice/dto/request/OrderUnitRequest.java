package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class OrderUnitRequest {

    @Schema(description = "Order Unit Code", example = "OU001")
    private String ouCode;

    @Schema(description = "Order Unit Name", example = "Order Unit Name")
    private String ouName;

    @Schema(description = "Order Unit Status", example = "true")
    private Boolean ouStatus;
}

