package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;

import com.example.mrpdataservice.entity.ProcurementType;
import com.example.mrpdataservice.request.ProcurementTypeRequest;
import com.example.mrpdataservice.response.ProcurementTypeResponse;

@Mapper(componentModel = "spring")
public interface ProcurementTypeMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	ProcurementType mapToProcurementType(ProcurementTypeRequest procurementTypeRequest);

	ProcurementTypeResponse mapToProcurementTypeResponse(ProcurementType procurementType);
}
