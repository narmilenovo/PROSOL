package com.example.createtemplateservice.client;

import java.util.Date;
import java.util.List;

import com.example.createtemplateservice.client.generalsettings.NmUomResponse;
import com.example.createtemplateservice.jpa.entity.Type;
import com.example.createtemplateservice.jpa.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryAllResponse {
	private Long id;
	private String noun;
	private String nounSynonyms;
	private String nmAbbreviation;
	private String modifier;
	private String modifierSynonyms;
	private String nmDefinition;
	private Type type;
	private String similarSearchItems;
	private List<NmUomResponse> nmUoms;
	private List<DictionaryAttributeAllResponse> attributes;
	private String image;
	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
