package com.example.createtemplateservice.client.valuemaster;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.createtemplateservice.jpa.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMasterResponse {

	private Long id;
	private String value;
	private String abbreviation;
	private Long abbreviationUnit;
	private String equivalent;
	private Long equivalentUnit;
	private String likelyWords;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;

}
