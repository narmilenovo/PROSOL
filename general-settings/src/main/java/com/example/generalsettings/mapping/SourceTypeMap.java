package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.SourceType;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;

@Mapper(componentModel = "spring")
public interface SourceTypeMap {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	SourceType mapToSourceType(SourceTypeRequest sourceTypeRequest);

	SourceTypeResponse mapToSourceTypeResponse(SourceType sourceType);
}
