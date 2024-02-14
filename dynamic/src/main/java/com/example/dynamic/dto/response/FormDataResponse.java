package com.example.dynamic.dto.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormDataResponse {

	private Long id;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;
}
