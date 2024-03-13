package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.entity.TaxClassificationType;

@Mapper(componentModel = "spring")
public interface TaxClassificationTypeMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	TaxClassificationType mapToTaxClassificationType(TaxClassificationTypeRequest typeRequest);

	TaxClassificationTypeResponse mapToTaxClassificationClassResponse(TaxClassificationType taxClassificationType);
}
