package com.example.createtemplateservice.client;

import java.util.Date;
import java.util.List;

import com.example.createtemplateservice.jpa.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeUomResponse {
	private Long id;
	private String attributeUomName;
	private String attributeUomUnit;
	private Boolean attributeUomStatus;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
