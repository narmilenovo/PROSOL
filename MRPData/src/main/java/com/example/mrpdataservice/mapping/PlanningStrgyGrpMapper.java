package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;

import com.example.mrpdataservice.entity.PlanningStrategyGrp;
import com.example.mrpdataservice.request.PlanningStrgyGrpRequest;
import com.example.mrpdataservice.response.PlanningStrgyGrpResponse;

@Mapper(componentModel = "spring")
public interface PlanningStrgyGrpMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	PlanningStrategyGrp mapToPlanningStrategyGrp(PlanningStrgyGrpRequest planningStrgyGrpRequest);

	PlanningStrgyGrpResponse mapToPlanningStrgyGrpResponse(PlanningStrategyGrp planningStrategyGrp);
}
