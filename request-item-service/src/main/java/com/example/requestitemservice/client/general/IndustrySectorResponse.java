package com.example.requestitemservice.client.general;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.requestitemservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class IndustrySectorResponse {
	private Long id;
	private String sectorCode;
	private String sectorName;
	private Boolean sectorStatus;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
