package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;

import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.entity.TransportationGroup;

@Mapper(componentModel = "spring")
public interface TransportationGroupMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	TransportationGroup mapToTransportationGroup(TransportationGroupRequest transportationGroupRequest);

	TransportationGroupResponse mapToTransportationGroupResponse(TransportationGroup taxClassificationType);

}
