package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.AttributeUom;
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;

@Mapper(componentModel = "spring")
public interface AttributeUomMap {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	AttributeUom mapToAttributeUom(AttributeUomRequest attributeUomRequest);

	AttributeUomResponse mapToAttributeUomResponse(AttributeUom attributeUom);
}
