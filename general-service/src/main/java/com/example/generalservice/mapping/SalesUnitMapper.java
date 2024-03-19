package com.example.generalservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.entity.SalesUnit;

@Mapper(componentModel = "spring")
public interface SalesUnitMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	SalesUnit mapToSalesUnit(SalesUnitRequest salesUnitRequest);

	SalesUnitResponse mapToSalesUnitResponse(SalesUnit salesUnit);

}
