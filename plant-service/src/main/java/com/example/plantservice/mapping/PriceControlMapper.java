package com.example.plantservice.mapping;

import org.mapstruct.Mapper;

import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.entity.PriceControl;

@Mapper(componentModel = "spring")
public interface PriceControlMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	PriceControl mapToPriceControl(PriceControlRequest priceControlRequest);

	PriceControlResponse mapToPriceControlResponse(PriceControl pricecontrol);
}
