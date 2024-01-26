package com.example.plantservice.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitCenterRequest {

	@Schema(description = "Profit Center Code", example = "PC-001")
	private String profitCenterCode;

	@Schema(description = "Profit Center Name", example = "Profit Center 1")
	private String profitCenterName;

	@Schema(description = "Profit Center Status", example = "true", defaultValue = "true", allowableValues = "true,false")
	private Boolean profitCenterStatus;

	@Schema(description = "Select Plant", example = "1", defaultValue = "1")
	private Long plantId;

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
