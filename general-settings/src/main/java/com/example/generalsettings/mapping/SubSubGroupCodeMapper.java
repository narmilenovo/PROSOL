package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.SubSubGroup;
import com.example.generalsettings.request.SubSubGroupRequest;
import com.example.generalsettings.response.SubSubGroupResponse;

@Mapper(componentModel = "spring")
public interface SubSubGroupCodeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "mainGroupCodesId.id", source = "mainGroupCodesId")
	@Mapping(target = "subGroupCodesId.id", source = "subGroupId")
	SubSubGroup mapToSubSubGroup(SubSubGroupRequest subSubGroupRequest);

	SubSubGroupResponse mapToSubChildGroupResponse(SubSubGroup subChildGroup);

}
