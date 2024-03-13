package com.example.generalservice.mapping;

import org.mapstruct.Mapper;

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.entity.Division;

@Mapper(componentModel = "spring")
public interface DivisionMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	Division mapToDivision(DivisionRequest divisionRequest);

	DivisionResponse mapToDivisionResponse(Division division);
}
