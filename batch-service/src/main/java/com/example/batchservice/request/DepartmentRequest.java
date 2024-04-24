package com.example.batchservice.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentRequest {

	private String departmentName;

	private Boolean departmentStatus;

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
