package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.entity.TaxClassificationClass;

@Mapper(componentModel = "spring")
public interface TaxClassificationClassMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	TaxClassificationClass mapToTaxClassificationClass(TaxClassificationClassRequest taxClassificationClassRequest);

	TaxClassificationClassResponse mapToTaxClassificationClassResponse(TaxClassificationClass taxClassificationClass);
}
