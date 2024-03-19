package com.example.plantservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.entity.ValuationCategory;

@Mapper(componentModel = "spring")
public interface ValuationCategoryMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	ValuationCategory mapToValuationCategory(ValuationCategoryRequest valuationCategoryRequest);

	ValuationCategoryResponse mapToValuationCategoryResponse(ValuationCategory valuationCategory);
}
