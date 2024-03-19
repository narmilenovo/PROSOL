package com.example.attributemaster.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.attributemaster.entity.FieldType;
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
public class AttributeMasterRequest {
	@Schema(description = "Attribute Name")
	private String attributeName;

	@Schema(description = "Field Type")
	private FieldType fieldType;

	@Schema(description = " Select Uom")
	private List<Long> listUom;

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
