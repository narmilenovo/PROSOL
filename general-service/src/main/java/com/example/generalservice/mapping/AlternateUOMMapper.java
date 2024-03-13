package com.example.generalservice.mapping;

import org.mapstruct.Mapper;

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.entity.AlternateUOM;

@Mapper(componentModel = "spring")
public interface AlternateUOMMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	AlternateUOM mapToAlternateUOM(AlternateUOMRequest alternateUOMRequest);

	AlternateUOMResponse mapToAlternateUOMResponse(AlternateUOM alternateUOM);
}
