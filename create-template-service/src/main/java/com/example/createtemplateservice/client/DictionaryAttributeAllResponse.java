package com.example.createtemplateservice.client;

import java.util.List;

import com.example.createtemplateservice.client.attributemaster.AttributeMasterUomResponse;
import com.example.createtemplateservice.client.valuemaster.ValueAttributeUom;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryAttributeAllResponse {
	private Long id;
	private AttributeMasterUomResponse attribute;
	private Integer shortPriority;
	private Boolean mandatory;
	private String definition;
	private List<ValueAttributeUom> values;
	private Boolean uomMandatory;
	private List<AttributeUomResponse> attrUoms;
}
