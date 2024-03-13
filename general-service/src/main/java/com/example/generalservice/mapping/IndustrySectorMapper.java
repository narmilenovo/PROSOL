package com.example.generalservice.mapping;

import org.mapstruct.Mapper;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.entity.IndustrySector;

@Mapper(componentModel = "spring")
public interface IndustrySectorMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	IndustrySector mapToIndustrySector(IndustrySectorRequest industrySectorRequest);

	IndustrySectorResponse mapToIndustrySectorResponse(IndustrySector industrySector);
}
