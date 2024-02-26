package com.example.mrpdataservice.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.mrpdataservice.client.Plant.PlantResponse;
import com.example.mrpdataservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MrpPlantResponse {
	private Long id;
	private String mrpControlCode;
	private String mrpControlName;
	private PlantResponse plant;
	private Boolean mrpControlStatus;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
