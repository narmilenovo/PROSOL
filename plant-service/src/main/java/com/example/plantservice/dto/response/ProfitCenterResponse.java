package com.example.plantservice.dto.response;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ProfitCenterResponse {
	private Long id;
	private String profitCenterCode;
	private String profitCenterName;
	private Boolean profitCenterStatus;
	private PlantResponse plant;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;
}
