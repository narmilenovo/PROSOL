package com.example.valueservice.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.valueservice.client.GeneralSetting.AttributeUomResponse;
import com.example.valueservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueAttributeUom {
	private Long id;
	private String value;
	private String abbreviation;
	private AttributeUomResponse abbreviationUnit;
	private String equivalent;
	private AttributeUomResponse equivalentUnit;
	private String likelyWords;
	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;

}
