package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.clients.DpPlant;
import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.entity.DeliveringPlant;

@Mapper(componentModel = "spring")
public interface DeliveringPlantMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	DeliveringPlant mapToDeliveringPlant(DeliveringPlantRequest deliveringPlantRequest);

	DeliveringPlantResponse mapToDeliveringPlantResponse(DeliveringPlant deliveringPlant);

	@Mapping(target = "plant", ignore = true)
	DpPlant mapToDpPlant(DeliveringPlant deliveringPlant);
}
