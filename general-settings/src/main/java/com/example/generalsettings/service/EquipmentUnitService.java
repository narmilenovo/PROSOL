package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.EquipmentUnit;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;

import jakarta.validation.Valid;

public interface EquipmentUnitService {

	EquipmentUnitResponse saveEquipmentUnit(@Valid EquipmentUnitRequest equipmentUnitRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	EquipmentUnitResponse getEquipmentUnitById(Long id) throws ResourceNotFoundException;

	List<EquipmentUnitResponse> getAllEquipmentUnit();

	List<EquipmentUnit> findAll();

	EquipmentUnitResponse updateEquipmentUnit(Long id, EquipmentUnitRequest equipmentUnitRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	EquipmentUnitResponse updateStatusUsingEquipmentUnitId(Long id) throws ResourceNotFoundException;

	List<EquipmentUnitResponse> updateBulkStatusEquipmentUnitId(List<Long> id) throws ResourceNotFoundException;

	void deleteEquipmentUnit(Long id) throws ResourceNotFoundException;

	void deleteBatchEquipmentUnit(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertEquipmentUnitListToMap(List<EquipmentUnit> equipmentUnitReport);
}
