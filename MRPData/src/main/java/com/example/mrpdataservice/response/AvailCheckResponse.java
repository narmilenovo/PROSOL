package com.example.mrpdataservice.response;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AvailCheckResponse {
	private Long id;
	private String availCheckCode;
	private String availCheckName;
	private Boolean availCheckStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;
}
