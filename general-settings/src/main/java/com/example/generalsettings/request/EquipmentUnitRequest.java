package com.example.generalsettings.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentUnitRequest {

    @Schema(description = "Equipment Unit Code", example = "EUC001")
    private String equipmentUnitCode;

    @Schema(description = "Equipment Unit Name")
    private String equipmentUnitName;

    @Schema(description = "Equipment Unit Status", example = "true")
    private Boolean equipmentUnitStatus;
}
