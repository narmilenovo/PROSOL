package com.example.generalservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.entity.InspectionType;

@Mapper(componentModel = "spring")
public interface InspectionTypeMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	InspectionType mapToInspectionType(InspectionTypeRequest inspectionTypeRequest);

	InspectionTypeResponse mapToInspectionTypeResponse(InspectionType inspectionType);
}
