package com.example.plantservice.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.plantservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
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
