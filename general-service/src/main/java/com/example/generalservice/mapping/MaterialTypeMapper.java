package com.example.generalservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.entity.MaterialType;

@Mapper(componentModel = "spring")
public interface MaterialTypeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	MaterialType mapToMaterialType(MaterialTypeRequest materialTypeRequest);

	MaterialTypeResponse mapToMaterialTypeResponse(MaterialType materialType);
}
