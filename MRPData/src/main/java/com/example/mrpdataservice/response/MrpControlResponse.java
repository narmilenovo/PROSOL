package com.example.mrpdataservice.response;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class MrpControlResponse {
	private Long id;
	private String mrpControlCode;
	private String mrpControlName;
	private Long plantId;
	private Boolean mrpControlStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;
}
