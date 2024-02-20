package com.example.mrpdataservice.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.mrpdataservice.entity.UpdateAuditHistory;
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

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
