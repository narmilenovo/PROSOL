package com.example.generalsettings.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceTypeRequest {
    @Schema(description = "Source Type Code", example = "STC-001")
    private String sourceTypeCode;

    @Schema(description = "Source Type Name")
    private String sourceTypeName;

    @Schema(description = "Source Type Status", example = "true")
    private Boolean sourceTypeStatus;
}
