package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.SubGroupCodes;
import com.example.generalsettings.request.SubGroupCodesRequest;
import com.example.generalsettings.response.SubGroupCodesResponse;

@Mapper(componentModel = "spring")
public interface SubGroupMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "mainGroupCodesId.id", source = "mainGroupCodesId")
	SubGroupCodes mapToSubGroupCodes(SubGroupCodesRequest subGroupCodesRequest);

	SubGroupCodesResponse mapToSubMainGroupResponse(SubGroupCodes subGroupCodes);
}
