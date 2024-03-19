package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.NmUom;
import com.example.generalsettings.request.NmUomRequest;
import com.example.generalsettings.response.NmUomResponse;

@Mapper(componentModel = "spring")
public interface NmUomMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	NmUom mapToNmUom(NmUomRequest nmUomRequest);

	NmUomResponse mapToNmUomResponse(NmUom nmUom);

}
