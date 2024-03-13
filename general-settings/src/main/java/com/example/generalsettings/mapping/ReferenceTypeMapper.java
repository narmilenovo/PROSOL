package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;

import com.example.generalsettings.entity.ReferenceType;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;

@Mapper(componentModel = "spring")
public interface ReferenceTypeMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	ReferenceType mapToReferenceType(ReferenceTypeRequest referenceTypeRequest);

	ReferenceTypeResponse mapToReferenceTypeResponse(ReferenceType referenceType);
}
