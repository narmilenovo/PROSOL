package com.example.generalsettings.response;

import lombok.Data;

@Data
public class EquipmentUnitResponse {
    private Long id;
    private String equipmentUnitCode;
    private String equipmentUnitName;
    private Boolean equipmentUnitStatus;
}
