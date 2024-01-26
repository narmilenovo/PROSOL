package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.EquipmentUnit;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.EquipmentUnitRepo;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;
import com.example.generalsettings.service.EquipmentUnitService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentUnitServiceImpl implements EquipmentUnitService {
	private final ModelMapper modelMapper;
	private final EquipmentUnitRepo equipmentUnitRepo;

	public static final String ATTRIBUTE_TYPE_NOT_FOUND_MESSAGE = null;

	@Override
	public EquipmentUnitResponse saveEquipmentUnit(EquipmentUnitRequest equipmentUnitRequest)
			throws AlreadyExistsException {
		boolean exists = equipmentUnitRepo.existsByEquipmentUnitCodeAndEquipmentUnitName(
				equipmentUnitRequest.getEquipmentUnitCode(), equipmentUnitRequest.getEquipmentUnitName());
		if (!exists) {
			EquipmentUnit equipmentUnit = modelMapper.map(equipmentUnitRequest, EquipmentUnit.class);
			equipmentUnitRepo.save(equipmentUnit);
			return mapToEquipmentUnitResponse(equipmentUnit);
		} else {
			throw new AlreadyExistsException("EquipmentUnit with this name already exists");
		}
	}

	@Override
	public EquipmentUnitResponse getEquipmentUnitById(Long id) throws ResourceNotFoundException {
		EquipmentUnit equipmentUnit = this.findEquipmentUnitById(id);
		return mapToEquipmentUnitResponse(equipmentUnit);
	}

	@Override
	public List<EquipmentUnitResponse> getAllEquipmentUnit() {
		List<EquipmentUnit> equipmentUnit = equipmentUnitRepo.findAllByOrderByIdAsc();
		return equipmentUnit.stream().map(this::mapToEquipmentUnitResponse).toList();
	}

	@Override
	public List<EquipmentUnit> findAll() {
		return equipmentUnitRepo.findAllByOrderByIdAsc();
	}

	@Override
	public EquipmentUnitResponse updateEquipmentUnit(Long id, EquipmentUnitRequest equipmentUnitRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String name = equipmentUnitRequest.getEquipmentUnitName();
		String code = equipmentUnitRequest.getEquipmentUnitCode();
		boolean exists = equipmentUnitRepo.existsByEquipmentUnitCodeAndEquipmentUnitNameAndIdNot(code, name, id);
		if (!exists) {
			EquipmentUnit existingEquipmentUnit = this.findEquipmentUnitById(id);
			modelMapper.map(equipmentUnitRequest, existingEquipmentUnit);
			equipmentUnitRepo.save(existingEquipmentUnit);
			return mapToEquipmentUnitResponse(existingEquipmentUnit);
		} else {
			throw new AlreadyExistsException("EquipmentUnit with this name already exists");
		}
	}

	@Override
	public List<EquipmentUnitResponse> updateBulkStatusEquipmentUnitId(List<Long> id) throws ResourceNotFoundException {
		List<EquipmentUnit> existingEquipmentUnit = this.findAllUnitsById(id);
		for (EquipmentUnit equipmentUnit : existingEquipmentUnit) {
			equipmentUnit.setEquipmentUnitStatus(!equipmentUnit.getEquipmentUnitStatus());
		}
		equipmentUnitRepo.saveAll(existingEquipmentUnit);
		return existingEquipmentUnit.stream().map(this::mapToEquipmentUnitResponse).toList();
	}

	@Override
	public EquipmentUnitResponse updateStatusUsingEquipmentUnitId(Long id) throws ResourceNotFoundException {
		EquipmentUnit existingEquipmentUnit = this.findEquipmentUnitById(id);
		existingEquipmentUnit.setEquipmentUnitStatus(!existingEquipmentUnit.getEquipmentUnitStatus());
		equipmentUnitRepo.save(existingEquipmentUnit);
		return mapToEquipmentUnitResponse(existingEquipmentUnit);
	}

	@Override
	public void deleteEquipmentUnit(Long id) throws ResourceNotFoundException {
		EquipmentUnit equipmentUnit = this.findEquipmentUnitById(id);
		equipmentUnitRepo.deleteById(equipmentUnit.getId());
	}

	@Override
	public void deleteBatchEquipmentUnit(List<Long> ids) throws ResourceNotFoundException {
		this.findAllUnitsById(ids);
		equipmentUnitRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertEquipmentUnitListToMap(List<EquipmentUnit> equipmentUnitList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (EquipmentUnit unit : equipmentUnitList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getId());
			data.put("Name", unit.getEquipmentUnitName());
			data.put("Status", unit.getEquipmentUnitStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private EquipmentUnitResponse mapToEquipmentUnitResponse(EquipmentUnit equipmentUnit) {
		return modelMapper.map(equipmentUnit, EquipmentUnitResponse.class);
	}

	private EquipmentUnit findEquipmentUnitById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<EquipmentUnit> equipmentUnit = equipmentUnitRepo.findById(id);
		if (equipmentUnit.isEmpty()) {
			throw new ResourceNotFoundException(ATTRIBUTE_TYPE_NOT_FOUND_MESSAGE);
		}
		return equipmentUnit.get();
	}

	private List<EquipmentUnit> findAllUnitsById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<EquipmentUnit> units = equipmentUnitRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> units.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return units;
	}
}
