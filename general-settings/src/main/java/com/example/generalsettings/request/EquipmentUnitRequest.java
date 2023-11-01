package com.example.generalsettings.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentUnitRequest {
    private String equipmentUnitCode;
    private String equipmentUnitName;
    private Boolean equipmentUnitStatus;
}
