package com.example.sales_otherservice.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@JsonIgnore
	private Map<String, Object> dynamicFields = new HashMap<>(); // Changed the value type to String

	@JsonAnyGetter
	public Map<String, Object> getDynamicFields() {
		return dynamicFields;
	}

	@JsonAnySetter
	public void setDynamicFields(String key, Object value) {
		this.dynamicFields.put(key, value);
	}
}
