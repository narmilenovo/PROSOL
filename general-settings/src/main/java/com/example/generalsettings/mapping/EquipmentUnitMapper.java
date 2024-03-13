package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;

import com.example.generalsettings.entity.EquipmentUnit;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;

@Mapper(componentModel = "spring")
public interface EquipmentUnitMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	EquipmentUnit mapToEquipmentUnit(EquipmentUnitRequest equipmentUnitRequest);

	EquipmentUnitResponse mapToEquipmentUnitResponse(EquipmentUnit equipmentUnit);
}
