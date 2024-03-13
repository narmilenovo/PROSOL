package com.example.plantservice.mapping;

import org.mapstruct.Mapper;

import com.example.plantservice.client.ValuationMaterialResponse;
import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.entity.ValuationClass;

@Mapper(componentModel = "spring")
public interface ValuationClassMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	ValuationClass mapToValuationClass(ValuationClassRequest valuationClassRequest);

	ValuationClassResponse mapToValuationClassResponse(ValuationClass valuationClass);

//	@Mapping(target = "material", ignore = true)
	ValuationMaterialResponse mapToValuationMaterialResponse(ValuationClass valuationClass);
}
