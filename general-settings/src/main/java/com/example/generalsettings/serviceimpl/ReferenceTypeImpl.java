package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.ReferenceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.ReferenceTypeRepo;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;
import com.example.generalsettings.service.ReferenceTypeService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReferenceTypeImpl implements ReferenceTypeService {
	private final ReferenceTypeRepo referenceTypeRepo;

	private final ModelMapper modelMapper;

	@Override
	public ReferenceTypeResponse saveReferenceType(ReferenceTypeRequest referenceTypeRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(referenceTypeRequest);
		boolean exists = referenceTypeRepo.existsByReferenceTypeCodeAndReferenceTypeName(
				referenceTypeRequest.getReferenceTypeCode(), referenceTypeRequest.getReferenceTypeName());
		if (!exists) {
			ReferenceType referenceType = modelMapper.map(referenceTypeRequest, ReferenceType.class);
			referenceTypeRepo.save(referenceType);
			return mapToReferenceTypeResponse(referenceType);
		} else {
			throw new AlreadyExistsException("ReferenceType with this name already exists");
		}
	}

	@Override
	public ReferenceTypeResponse getReferenceTypeById(Long id) throws ResourceNotFoundException {
		ReferenceType referenceType = this.findReferenceTypeById(id);
		return mapToReferenceTypeResponse(referenceType);
	}

	@Override
	public List<ReferenceTypeResponse> getAllReferenceType() {
		List<ReferenceType> referenceType = referenceTypeRepo.findAllByOrderByIdAsc();
		return referenceType.stream().map(this::mapToReferenceTypeResponse).toList();
	}

	@Override
	public List<ReferenceType> findAll() {
		return referenceTypeRepo.findAllByOrderByIdAsc();
	}

	@Override
	public ReferenceTypeResponse updateReferenceType(Long id, ReferenceTypeRequest referenceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(referenceTypeRequest);
		String name = referenceTypeRequest.getReferenceTypeName();
		String code = referenceTypeRequest.getReferenceTypeCode();
		boolean exists = referenceTypeRepo.existsByReferenceTypeCodeAndReferenceTypeNameAndIdNot(code, name, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			ReferenceType existingReferenceType = this.findReferenceTypeById(id);
			if (!existingReferenceType.getReferenceTypeCode().equals(code)) {
				auditFields.add(new AuditFields(null, "ReferenceType Code",
						existingReferenceType.getReferenceTypeCode(), code));
				existingReferenceType.setReferenceTypeCode(code);
			}
			if (!existingReferenceType.getReferenceTypeName().equals(name)) {
				auditFields.add(new AuditFields(null, "ReferenceType Name",
						existingReferenceType.getReferenceTypeName(), name));
				existingReferenceType.setReferenceTypeName(name);
			}
			if (!existingReferenceType.getReferenceTypeStatus().equals(referenceTypeRequest.getReferenceTypeStatus())) {
				auditFields.add(new AuditFields(null, "ReferenceType Status",
						existingReferenceType.getReferenceTypeStatus(), referenceTypeRequest.getReferenceTypeStatus()));
				existingReferenceType.setReferenceTypeStatus(referenceTypeRequest.getReferenceTypeStatus());
			}
			if (!existingReferenceType.getDuplicateCheck().equals(referenceTypeRequest.getDuplicateCheck())) {
				auditFields.add(new AuditFields(null, "Duplicate Check", existingReferenceType.getDuplicateCheck(),
						referenceTypeRequest.getDuplicateCheck()));
				existingReferenceType.setDuplicateCheck(referenceTypeRequest.getDuplicateCheck());
			}
			existingReferenceType.updateAuditHistory(auditFields);
			referenceTypeRepo.save(existingReferenceType);
			return mapToReferenceTypeResponse(existingReferenceType);
		} else {
			throw new AlreadyExistsException("ReferenceType with this name already exists");
		}
	}

	@Override
	public List<ReferenceTypeResponse> updateBulkStatusReferenceTypeId(List<Long> id) throws ResourceNotFoundException {
		List<ReferenceType> existingReferenceTypeList = this.findAllRefTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingReferenceTypeList.forEach(existingReferenceType -> {
			if (existingReferenceType.getReferenceTypeStatus() != null) {
				auditFields.add(
						new AuditFields(null, "ReferenceType Status", existingReferenceType.getReferenceTypeStatus(),
								!existingReferenceType.getReferenceTypeStatus()));
				existingReferenceType.setReferenceTypeStatus(!existingReferenceType.getReferenceTypeStatus());
			}
			existingReferenceType.updateAuditHistory(auditFields);
		});
		referenceTypeRepo.saveAll(existingReferenceTypeList);
		return existingReferenceTypeList.stream().map(this::mapToReferenceTypeResponse).toList();
	}

	@Override
	public ReferenceTypeResponse updateStatusUsingReferenceTypeId(Long id) throws ResourceNotFoundException {
		ReferenceType existingReferenceType = this.findReferenceTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingReferenceType.getReferenceTypeStatus() != null) {
			auditFields.add(new AuditFields(null, "ReferenceType Status",
					existingReferenceType.getReferenceTypeStatus(), !existingReferenceType.getReferenceTypeStatus()));
			existingReferenceType.setReferenceTypeStatus(!existingReferenceType.getReferenceTypeStatus());
		}
		existingReferenceType.updateAuditHistory(auditFields);
		referenceTypeRepo.save(existingReferenceType);
		return mapToReferenceTypeResponse(existingReferenceType);
	}

	@Override
	public ReferenceTypeResponse updateRefrenceDupCheckById(Long id) throws ResourceNotFoundException {
		ReferenceType existingReferenceType = this.findReferenceTypeById(id);
		existingReferenceType.setDuplicateCheck(!existingReferenceType.getDuplicateCheck());
		referenceTypeRepo.save(existingReferenceType);
		return mapToReferenceTypeResponse(existingReferenceType);
	}

	@Override
	public List<ReferenceTypeResponse> updateBulkReferenceDupCheckTypeId(List<Long> id)
			throws ResourceNotFoundException {
		List<ReferenceType> existingReferenceType = this.findAllRefTypeById(id);
		for (ReferenceType referenceType : existingReferenceType) {
			referenceType.setDuplicateCheck(!referenceType.getDuplicateCheck());
		}
		referenceTypeRepo.saveAll(existingReferenceType);
		return existingReferenceType.stream().map(this::mapToReferenceTypeResponse).toList();
	}

	@Override
	public void deleteReferenceType(Long id) throws ResourceNotFoundException {
		ReferenceType referenceType = this.findReferenceTypeById(id);
		referenceTypeRepo.deleteById(referenceType.getId());
	}

	@Override
	public void deleteBatchReferenceType(List<Long> ids) throws ResourceNotFoundException {
		this.findAllRefTypeById(ids);
		referenceTypeRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertReferenceTypeListToMap(List<ReferenceType> referenceTypeList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (ReferenceType unit : referenceTypeList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getId());
			data.put("Name", unit.getReferenceTypeName());
			data.put("Status", unit.getReferenceTypeStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private ReferenceTypeResponse mapToReferenceTypeResponse(ReferenceType referenceType) {
		return modelMapper.map(referenceType, ReferenceTypeResponse.class);
	}

	private ReferenceType findReferenceTypeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ReferenceType> referenceType = referenceTypeRepo.findById(id);
		if (referenceType.isEmpty()) {
			throw new ResourceNotFoundException("ReferenceType with ID " + id + " not found");
		}
		return referenceType.get();
	}

	private List<ReferenceType> findAllRefTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ReferenceType> types = referenceTypeRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> types.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Refrence Tyoe with IDs " + missingIds + " not found.");
		}
		return types;
	}

}
