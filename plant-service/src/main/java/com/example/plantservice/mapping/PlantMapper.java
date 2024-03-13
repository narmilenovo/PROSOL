package com.example.plantservice.mapping;

import org.mapstruct.Mapper;

import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.entity.Plant;

@Mapper(componentModel = "spring")
public interface PlantMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	Plant mapToPlant(PlantRequest plantRequest);

	PlantResponse mapToPlantResponse(Plant plant);
}
