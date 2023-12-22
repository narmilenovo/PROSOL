package com.example.createtemplateservice.jpa.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DictionaryAttributeRequest {

    @Schema(description = "Select Attribute", example = "1")
    private Long attributeId;

    @Schema(description = "short priority", example = "1")
    private Integer shortPriority;

    @Schema(description = "Mandatory", example = "true")
    private Boolean mandatory;

    @Schema(description = "Definition", example = "Definition")
    private String definition;

    @Schema(description = "Select Value")
    private List<Long> valueId;

    @Schema(description = "UOM Mandatory", example = "true", allowableValues = "true,false", defaultValue = "false")
    private Boolean uomMandatory;

    @Schema(description = "Select Attribute UOM")
    private List<Long> attrUomId;
}
