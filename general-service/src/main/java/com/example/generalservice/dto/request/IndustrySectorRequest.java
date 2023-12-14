package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndustrySectorRequest {

    @Schema(description = "Sector Code", example = "SC-001")
    private String sectorCode;

    @Schema(description = "Sector Name")
    private String sectorName;

    @Schema(description = "Sector Status", example = "true")
    private Boolean sectorStatus;
}
