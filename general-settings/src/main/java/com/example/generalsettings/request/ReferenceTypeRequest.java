package com.example.generalsettings.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceTypeRequest {

    @Schema(description = "Reference Type Code", example = "1")
    private String referenceTypeCode;

    @Schema(description = "Reference Type Name")
    private String referenceTypeName;

    @Schema(description = "Reference Type Status", example = "true")
    private Boolean referenceTypeStatus;

    @Schema(description = "Duplicate Check", example = "true")
    private Boolean duplicateCheck;
}
