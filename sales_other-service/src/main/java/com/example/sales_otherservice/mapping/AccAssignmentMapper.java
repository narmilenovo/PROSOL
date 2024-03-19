package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.entity.AccAssignment;

@Mapper(componentModel = "spring")
public interface AccAssignmentMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	AccAssignment mapToAccAssignment(AccAssignmentRequest accAssignmentRequest);

	AccAssignmentResponse mapToAccAssignmentResponse(AccAssignment accAssignment);

}
