package com.example.plantservice.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.plantservice.client.General.MaterialTypeResponse;
import com.example.plantservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValuationMaterialResponse {
	private Long id;
	private String valuationClassCode;
	private String valuationClassName;
	private MaterialTypeResponse material;
	private Boolean valuationClassStatus;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;

}
