package com.example.generalservice.mapping;

import org.mapstruct.Mapper;

import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.entity.BaseUOP;

@Mapper(componentModel = "spring")
public interface BaseUOPMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	BaseUOP mapToBaseUOP(BaseUOPRequest baseUOPRequest);

	BaseUOPResponse mapToBaseUOPResponse(BaseUOP baseUOP);
}
