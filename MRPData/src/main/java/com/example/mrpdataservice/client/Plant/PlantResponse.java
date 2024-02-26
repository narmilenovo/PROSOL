package com.example.mrpdataservice.client.Plant;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.mrpdataservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantResponse {
	private Long id;
	private String plantCode;
	private String plantName;
	private Boolean plantStatus;
	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
