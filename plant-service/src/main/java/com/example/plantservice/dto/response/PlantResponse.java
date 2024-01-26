package com.example.plantservice.dto.response;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class PlantResponse {
	private Long id;
	private String plantCode;
	private String plantName;
	private Boolean plantStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

}
