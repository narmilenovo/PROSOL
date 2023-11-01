package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;

import jakarta.validation.Valid;

public interface EquipmentUnitService {

	EquipmentUnitResponse saveEquipmentUnit(@Valid EquipmentUnitRequest equipmentUnitRequest)throws ResourceNotFoundException, AlreadyExistsException;

	EquipmentUnitResponse updateEquipmentUnit(Long id, EquipmentUnitRequest equipmentUnitRequest)throws ResourceNotFoundException, AlreadyExistsException;

	EquipmentUnitResponse getEquipmentUnitById(Long id)throws ResourceNotFoundException;

	void deleteEquipmentUnit(Long id)throws ResourceNotFoundException;

	EquipmentUnitResponse updateStatusUsingEquipmentUnitId(Long id)throws ResourceNotFoundException;

	List<EquipmentUnitResponse> updateBulkStatusEquipmentUnitId(List<Long> id);

	List<EquipmentUnitResponse> getAllEquipmentUnit();
}
