package com.example.dynamic.dto.response;

import java.util.Date;
import java.util.List;

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
	private String identity;

	private List<String> pattern;

	private Long min;
	private Long max;

	private Boolean isRequired;
	private Boolean isExtraField;
	private Boolean isReadable;
	private Boolean isWritable;
	private Boolean isUnique;

	private String displayRelationFieldName;

	private List<DropDownResponse> dropDowns;

	private List<String> enums;

	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;

}
