package com.example.generalsettings.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_EMPTY)
public class FormFieldResponse {

	private Long id;
	private String fieldName;
	private String dataType;

	private Long min;
	private Long max;

	private Boolean isRequired;
	private Boolean isExtraField;
	private Boolean isReadable;
	private Boolean isWritable;
}
