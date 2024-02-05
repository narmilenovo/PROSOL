package com.example.generalsettings.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeUomRequest {

	@Schema(description = "Attribute UOM Name")
	private String attributeUomName;

	@Schema(description = "Attribute UOM Unit", example = "UOM001")
	private String attributeUomUnit;

	@Schema(description = "Attribute UOM Status", example = "true")
	private Boolean attributeUomStatus;
}
