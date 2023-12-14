package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseUOPRequest {

    @Schema(description = "UOP Code", example = "UOP_001")
    private String uopCode;

    @Schema(description = "UOP Name")
    private String uopName;

    @Schema(description = "UOP Status", example = "true")
    private Boolean uopStatus;
}
