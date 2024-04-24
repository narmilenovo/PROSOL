package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.EquipmentUnit;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.EquipmentUnitMap;
import com.example.generalsettings.repo.EquipmentUnitRepo;
import com.example.generalsettings.request.EquipmentUnitRequest;
import com.example.generalsettings.response.EquipmentUnitResponse;
import com.example.generalsettings.service.EquipmentUnitService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipmentUnitServiceImpl implements EquipmentUnitService {
	private final EquipmentUnitMap equipmentUnitMapper;
	private final EquipmentUnitRepo equipmentUnitRepo;

	@Override
	public EquipmentUnitResponse saveEquipmentUnit(EquipmentUnitRequest equipmentUnitRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(equipmentUnitRequest);
		String equipmentUnitCode = equipmentUnitRequest.getEquipmentUnitCode();
		String equipmentUnitName = equipmentUnitRequest.getEquipmentUnitName();
		if (equipmentUnitRepo.existsByEquipmentUnitCodeAndEquipmentUnitName(equipmentUnitCode, equipmentUnitName)) {
			throw new AlreadyExistsException("EquipmentUnit with this name already exists");
		}
		EquipmentUnit equipmentUnit = equipmentUnitMapper.mapToEquipmentUnit(equipmentUnitRequest);
		equipmentUnitRepo.save(equipmentUnit);
		return equipmentUnitMapper.mapToEquipmentUnitResponse(equipmentUnit);
	}

	@Override
	public EquipmentUnitResponse getEquipmentUnitById(Long id) throws ResourceNotFoundException {
		EquipmentUnit equipmentUnit = this.findEquipmentUnitById(id);
		return equipmentUnitMapper.mapToEquipmentUnitResponse(equipmentUnit);
	}

	@Override
	public List<EquipmentUnitResponse> getAllEquipmentUnit() {
		return equipmentUnitRepo.findAllByOrderByIdAsc().stream().map(equipmentUnitMapper::mapToEquipmentUnitResponse)
				.toList();
	}

	@Override
	public List<EquipmentUnit> findAll() {
		return equipmentUnitRepo.findAllByOrderByIdAsc();
	}

	@Override
	public EquipmentUnitResponse updateEquipmentUnit(Long id, EquipmentUnitRequest equipmentUnitRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(equipmentUnitRequest);
		String name = equipmentUnitRequest.getEquipmentUnitName();
		String code = equipmentUnitRequest.getEquipmentUnitCode();
		boolean exists = equipmentUnitRepo.existsByEquipmentUnitCodeAndEquipmentUnitNameAndIdNot(code, name, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			EquipmentUnit existingEquipmentUnit = this.findEquipmentUnitById(id);
			if (!existingEquipmentUnit.getEquipmentUnitCode().equals(code)) {
				auditFields.add(new AuditFields(null, "Equipment Unit Code",
						existingEquipmentUnit.getEquipmentUnitCode(), code));
				existingEquipmentUnit.setEquipmentUnitCode(code);
			}
			if (!existingEquipmentUnit.getEquipmentUnitName().equals(name)) {
				auditFields.add(new AuditFields(null, "Equipment Unit Name",
						existingEquipmentUnit.getEquipmentUnitName(), name));
				existingEquipmentUnit.setEquipmentUnitName(name);
			}
			if (!existingEquipmentUnit.getEquipmentUnitStatus().equals(equipmentUnitRequest.getEquipmentUnitStatus())) {
				auditFields.add(new AuditFields(null, "Equipment Unit Status",
						existingEquipmentUnit.getEquipmentUnitStatus(), equipmentUnitRequest.getEquipmentUnitStatus()));
				existingEquipmentUnit.setEquipmentUnitStatus(equipmentUnitRequest.getEquipmentUnitStatus());

			}
			existingEquipmentUnit.updateAuditHistory(auditFields);
			equipmentUnitRepo.save(existingEquipmentUnit);
			return equipmentUnitMapper.mapToEquipmentUnitResponse(existingEquipmentUnit);
		} else {
			throw new AlreadyExistsException("EquipmentUnit with this name already exists");
		}
	}

	@Override
	public List<EquipmentUnitResponse> updateBulkStatusEquipmentUnitId(List<Long> id) throws ResourceNotFoundException {
		List<EquipmentUnit> existingEquipmentUnits = this.findAllUnitsById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingEquipmentUnits.forEach(existingEquipmentUnit -> {
			if (existingEquipmentUnit.getEquipmentUnitStatus() != null) {
				auditFields.add(
						new AuditFields(null, "Equipment Unit Status", existingEquipmentUnit.getEquipmentUnitStatus(),
								!existingEquipmentUnit.getEquipmentUnitStatus()));
				existingEquipmentUnit.setEquipmentUnitStatus(!existingEquipmentUnit.getEquipmentUnitStatus());
			}
			existingEquipmentUnit.updateAuditHistory(auditFields);

		});
		equipmentUnitRepo.saveAll(existingEquipmentUnits);
		return existingEquipmentUnits.stream().map(equipmentUnitMapper::mapToEquipmentUnitResponse).toList();
	}

	@Override
	public EquipmentUnitResponse updateStatusUsingEquipmentUnitId(Long id) throws ResourceNotFoundException {
		EquipmentUnit existingEquipmentUnit = this.findEquipmentUnitById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingEquipmentUnit.getEquipmentUnitStatus() != null) {
			auditFields.add(new AuditFields(null, "Equipment Unit Status",
					existingEquipmentUnit.getEquipmentUnitStatus(), !existingEquipmentUnit.getEquipmentUnitStatus()));
			existingEquipmentUnit.setEquipmentUnitStatus(!existingEquipmentUnit.getEquipmentUnitStatus());
		}
		existingEquipmentUnit.updateAuditHistory(auditFields);
		equipmentUnitRepo.save(existingEquipmentUnit);
		return equipmentUnitMapper.mapToEquipmentUnitResponse(existingEquipmentUnit);
	}

	@Override
	public void deleteEquipmentUnit(Long id) throws ResourceNotFoundException {
		EquipmentUnit equipmentUnit = this.findEquipmentUnitById(id);
		if (equipmentUnit != null) {
			equipmentUnitRepo.delete(equipmentUnit);
		}
	}

	@Override
	public void deleteBatchEquipmentUnit(List<Long> ids) throws ResourceNotFoundException {
		List<EquipmentUnit> equipmentUnits = this.findAllUnitsById(ids);
		if (!equipmentUnits.isEmpty()) {
			equipmentUnitRepo.deleteAll(equipmentUnits);
		}
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

	private EquipmentUnit findEquipmentUnitById(Long id) throws ResourceNotFoundException {
		return equipmentUnitRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Attribute type with " + id + " not found !!"));
	}

	private List<EquipmentUnit> findAllUnitsById(List<Long> ids) throws ResourceNotFoundException {
		List<EquipmentUnit> units = equipmentUnitRepo.findAllById(ids);
		Set<Long> idSet = new HashSet<>(ids);
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Equipment Unit with IDs " + missingIds + " not found.");
		}

		return units;
	}

}
