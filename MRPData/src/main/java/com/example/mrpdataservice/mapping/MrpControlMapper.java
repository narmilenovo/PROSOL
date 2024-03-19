package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mrpdataservice.client.MrpPlantResponse;
import com.example.mrpdataservice.entity.MrpControl;
import com.example.mrpdataservice.request.MrpControlRequest;
import com.example.mrpdataservice.response.MrpControlResponse;

@Mapper(componentModel = "spring")
public interface MrpControlMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	MrpControl mapToMrpControl(MrpControlRequest mrpControlRequest);

	MrpControlResponse mapToMrpControlResponse(MrpControl mrpControl);

	@Mapping(target = "plant.id", source = "plantId")
	MrpPlantResponse mapToMrpPlantResponse(MrpControl mrpControl);
}
