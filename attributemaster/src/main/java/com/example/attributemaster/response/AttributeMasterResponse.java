package com.example.attributemaster.response;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.attributemaster.entity.FieldType;
import com.example.attributemaster.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AttributeMasterResponse {
	private Long id;
	private String attributeName;
	private FieldType fieldType;
	private List<Long> listUom;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
