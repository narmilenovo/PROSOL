package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class TransportationGroupRequest {

    @Schema(description = "Transportation Group Code", example = "TG001")
    private String tgCode;


    @Schema(description = "Transportation Group Name", example = "Air")
    private String tgName;

    @Schema(description = "Transportation Group Status", example = "true", allowableValues = "true,false", defaultValue = "true")
    private Boolean tgStatus;
}

