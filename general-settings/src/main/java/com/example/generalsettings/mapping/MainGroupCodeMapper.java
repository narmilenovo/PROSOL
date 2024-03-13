package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;

import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;

@Mapper(componentModel = "spring")
public interface MainGroupCodeMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	MainGroupCodes mapToMainGroupCodes(MainGroupCodesRequest mainGroupCodesRequest);

	MainGroupCodesResponse mapToMainGroupCodesResponse(MainGroupCodes mainGroupCodes);
}
