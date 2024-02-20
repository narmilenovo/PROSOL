package com.example.mrpdataservice.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.mrpdataservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class MrpTypeResponse {
	private Long id;
	private String mrpTypeCode;
	private String mrpTypeName;
	private Boolean mrpTypeStatus;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
