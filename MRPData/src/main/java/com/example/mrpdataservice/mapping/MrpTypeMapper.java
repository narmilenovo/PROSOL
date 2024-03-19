package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mrpdataservice.entity.MrpType;
import com.example.mrpdataservice.request.MrpTypeRequest;
import com.example.mrpdataservice.response.MrpTypeResponse;

@Mapper(componentModel = "spring")
public interface MrpTypeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	MrpType mapToMrpType(MrpTypeRequest mrpTypeRequest);

	MrpTypeResponse mapToMrpTypeResponse(MrpType mrpType);
}
