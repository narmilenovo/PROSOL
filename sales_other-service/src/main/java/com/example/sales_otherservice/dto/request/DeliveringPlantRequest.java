package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveringPlantRequest {
    @Schema(description = "Delivering Plant Code", example = "DP001")
    private String dpCode;

    @Schema(description = "Delivering Plant Name", example = "Delivering Plant Name")
    private String dpName;

    @Schema(description = "Delivering Plant Status", example = "true")
    private Boolean dpStatus;

    @Schema(description = "Select Plant", example = "1")
    private Long plantId;
}
