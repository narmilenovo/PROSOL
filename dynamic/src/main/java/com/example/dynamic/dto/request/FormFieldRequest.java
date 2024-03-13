package com.example.dynamic.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldRequest {

	private String fieldName;
	private String dataType;
	private String identity;

	private List<String> pattern = new ArrayList<>();

	private Long min;
	private Long max;

	private Boolean isRequired = false;
	private Boolean isExtraField = true;
	private Boolean isReadable = true;
	private Boolean isWritable = true;
	private Boolean isUnique = false;

	private String displayRelationFieldName;

	private List<DropDownRequest> dropDowns = new ArrayList<>();

	private List<String> enums = new ArrayList<>();

	@JsonIgnore
	private FormRequest form;

}
