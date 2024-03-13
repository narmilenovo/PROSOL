package com.example.plantservice.mapping;

import org.mapstruct.Mapper;

import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.entity.VarianceKey;

@Mapper(componentModel = "spring")
public interface VarianceKeyMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	VarianceKey mapToVarianceKey(VarianceKeyRequest varianceKeyRequest);

	VarianceKeyResponse mapToVarianceKeyResponse(VarianceKey valuationCategory);
}
