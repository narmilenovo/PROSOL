package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.entity.MaterialStrategicGroup;

@Mapper(componentModel = "spring")
public interface MaterialStrategicGroupMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	MaterialStrategicGroup mapToStrategicGroup(MaterialStrategicGroupRequest strategicGroupRequest);

	MaterialStrategicGroupResponse mapToStrategicGroupResponse(MaterialStrategicGroup materialStrategicGroup);
}
