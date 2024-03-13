package com.example.generalservice.mapping;

import org.mapstruct.Mapper;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.entity.InspectionCode;

@Mapper(componentModel = "spring")
public interface InspectionCodeMapper {
//
//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	InspectionCode mapToInspectionCode(InspectionCodeRequest inspectionCodeRequest);

	InspectionCodeResponse mapToCodeResponse(InspectionCode inspectionCode);
}
