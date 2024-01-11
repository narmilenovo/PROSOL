package com.example.dynamic.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormResponse {

	private Long id;

	private String formName;

	private List<FieldResponse> fields;

	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
