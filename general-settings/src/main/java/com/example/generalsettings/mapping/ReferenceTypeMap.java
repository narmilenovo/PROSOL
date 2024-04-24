package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.ReferenceType;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;

@Mapper(componentModel = "spring")
public interface ReferenceTypeMap {
	@Mapping(target = "id", ignore = true, expression = "java(null)")
	@Mapping(target = "createdAt", ignore = true, expression = "java(null)")
	@Mapping(target = "createdBy", ignore = true, expression = "java(null)")
	@Mapping(target = "updateAuditHistories", ignore = true, expression = "java(null)")
	ReferenceType mapToReferenceType(ReferenceTypeRequest referenceTypeRequest);

	ReferenceTypeResponse mapToReferenceTypeResponse(ReferenceType referenceType);
}
