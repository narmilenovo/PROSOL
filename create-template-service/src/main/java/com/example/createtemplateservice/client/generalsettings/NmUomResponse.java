package com.example.createtemplateservice.client.generalsettings;

import java.util.Date;
import java.util.List;

import com.example.createtemplateservice.jpa.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmUomResponse {
	private Long id;
	private String nmUomName;
	private Boolean nmUomStatus;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
