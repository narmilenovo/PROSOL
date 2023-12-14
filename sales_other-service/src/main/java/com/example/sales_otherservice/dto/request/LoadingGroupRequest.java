package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoadingGroupRequest {
    @Schema(description = "Loading Group Code", example = "LG001")
    private String lgCode;

    @Schema(description = "Loading Group Name", example = "Loading Group 1")
    private String lgName;

    @Schema(description = "Loading Group Status", example = "true") // true = active, false = inactive
    private Boolean lgStatus;
}

