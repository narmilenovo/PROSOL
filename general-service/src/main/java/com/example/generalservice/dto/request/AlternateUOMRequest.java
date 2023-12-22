package com.example.generalservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlternateUOMRequest {

	@Schema(description = "UOM Code", example = "UOM001")
	private String uomCode;

	@Schema(description = "UOM Name", example = "KG")
	private String uomName;

	@Schema(description = "UOM Status", example = "true")
	private Boolean uomStatus;
}