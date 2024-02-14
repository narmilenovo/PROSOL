package com.example.dynamic.dto.request;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class FormDataRequest {

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
