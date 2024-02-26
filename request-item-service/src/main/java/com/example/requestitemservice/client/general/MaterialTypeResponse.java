package com.example.requestitemservice.client.general;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.requestitemservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class MaterialTypeResponse {
	private Long id;
	private String materialCode;
	private String materialName;
	private Boolean materialStatus;
	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
