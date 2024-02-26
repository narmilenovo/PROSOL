package com.example.attributemaster.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.attributemaster.client.GeneralSettings.AttributeUomResponse;
import com.example.attributemaster.entity.FieldType;
import com.example.attributemaster.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeMasterUomResponse {
	private Long id;
	private String attributeName;
	private FieldType fieldType;
	private List<AttributeUomResponse> listUom;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
