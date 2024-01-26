package com.example.generalsettings.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NmUomRequest {

    @Schema(description = "UOM Name", example = "KG")
    private String nmUomName;

    @Schema(description = "UOM Status", example = "true")
    private Boolean nmUomStatus;
}
