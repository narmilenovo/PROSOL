package com.example.generalsettings.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HsnRequest {

    @Schema(description = "hsn code", example = "HSN-001")
    private String hsnCode;

    @Schema(description = "hsn description", example = "hsn description")
    private String hsnDesc;

    @Schema(description = "hsn status", example = "true")
    private Boolean hsnStatus;
}
