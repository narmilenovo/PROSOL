package com.example.plantservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.entity.ProfitCenter;

@Mapper(componentModel = "spring")
public interface ProfitCenterMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "plant.id", source = "plantId")
	ProfitCenter mapToProfitCenter(ProfitCenterRequest profitCenterRequest);

	ProfitCenterResponse mapToProfitCenterResponse(ProfitCenter profitCenter);
}
