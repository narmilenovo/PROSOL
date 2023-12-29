package com.example.sales_otherservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ItemCategoryGroupRequest {

	@Schema(description = "Item Category Group Code", example = "ICG001")
	private String icgCode;

	@Schema(description = "Item Category Group Name", example = "ICG001 Name")
	private String icgName;

	@Schema(description = "Item Category Group Status", example = "true")
	private Boolean icgStatus;
}
