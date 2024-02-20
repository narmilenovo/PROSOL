package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.SourceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.SourceTypeRepo;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;
import com.example.generalsettings.service.SourceTypeService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SourceTypeServiceImpl implements SourceTypeService {
	private final SourceTypeRepo sourceTypeRepo;

	private final ModelMapper modelMapper;

	@Override
	public SourceTypeResponse saveSourceType(SourceTypeRequest sourceTypeRequest) throws AlreadyExistsException {
		Helpers.inputTitleCase(sourceTypeRequest);
		boolean exists = sourceTypeRepo.existsBySourceTypeCodeAndSourceTypeName(sourceTypeRequest.getSourceTypeCode(),
				sourceTypeRequest.getSourceTypeName());
		if (!exists) {
			SourceType sourceType = modelMapper.map(sourceTypeRequest, SourceType.class);
			sourceTypeRepo.save(sourceType);
			return mapToSourceTypeResponse(sourceType);
		} else {
			throw new AlreadyExistsException("SourceType with this name already exists");
		}
	}

	@Override
	public SourceTypeResponse getSourceTypeById(Long id) throws ResourceNotFoundException {
		SourceType sourceType = this.findSourceTypeById(id);
		return mapToSourceTypeResponse(sourceType);
	}

	@Override
	public List<SourceTypeResponse> getAllSourceType() {
		List<SourceType> sourceType = sourceTypeRepo.findAllByOrderByIdAsc();
		return sourceType.stream().map(this::mapToSourceTypeResponse).toList();
	}

	@Override
	public List<SourceType> findAll() {
		return sourceTypeRepo.findAllByOrderByIdAsc();
	}

	@Override
	public SourceTypeResponse updateSourceType(Long id, SourceTypeRequest sourceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(sourceTypeRequest);
		String name = sourceTypeRequest.getSourceTypeName();
		String code = sourceTypeRequest.getSourceTypeCode();
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		boolean exists = sourceTypeRepo.existsBySourceTypeCodeAndSourceTypeNameAndIdNot(code, name, id);
		if (!exists) {
			SourceType existingSourceType = this.findSourceTypeById(id);
			if (!existingSourceType.getSourceTypeName().equals(name)) {
				auditFields.add(new AuditFields(null, "SourceType Name", existingSourceType.getSourceTypeName(), name));
				existingSourceType.setSourceTypeName(name);
			}
			if (!existingSourceType.getSourceTypeCode().equals(code)) {
				auditFields.add(new AuditFields(null, "SourceType Code", existingSourceType.getSourceTypeCode(), code));
				existingSourceType.setSourceTypeCode(code);
			}
			if (!existingSourceType.getSourceTypeStatus().equals(sourceTypeRequest.getSourceTypeStatus())) {
				auditFields.add(new AuditFields(null, "SourceType Status", existingSourceType.getSourceTypeStatus(),
						sourceTypeRequest.getSourceTypeStatus()));
				existingSourceType.setSourceTypeStatus(sourceTypeRequest.getSourceTypeStatus());
			}
			existingSourceType.updateAuditHistory(auditFields);
			sourceTypeRepo.save(existingSourceType);
			return mapToSourceTypeResponse(existingSourceType);
		} else {
			throw new AlreadyExistsException("SourceType with this name already exists");
		}
	}

	@Override
	public List<SourceTypeResponse> updateBulkStatusSourceTypeId(List<Long> id) throws ResourceNotFoundException {
		List<SourceType> existingSourceTypeList = this.findAllSrcTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingSourceTypeList.forEach(existingSourceType -> {
			if (existingSourceType.getSourceTypeStatus() != null) {
				auditFields.add(new AuditFields(null, "SourceType Status", existingSourceType.getSourceTypeStatus(),
						!existingSourceType.getSourceTypeStatus()));
				existingSourceType.setSourceTypeStatus(!existingSourceType.getSourceTypeStatus());
			}
			existingSourceType.updateAuditHistory(auditFields);
			sourceTypeRepo.save(existingSourceType);
		});
		sourceTypeRepo.saveAll(existingSourceTypeList);
		return existingSourceTypeList.stream().map(this::mapToSourceTypeResponse).toList();
	}

	@Override
	public SourceTypeResponse updateStatusUsingSourceTypeId(Long id) throws ResourceNotFoundException {
		SourceType existingSourceType = this.findSourceTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingSourceType.getSourceTypeStatus() != null) {
			auditFields.add(new AuditFields(null, "SourceType Status", existingSourceType.getSourceTypeStatus(),
					!existingSourceType.getSourceTypeStatus()));
			existingSourceType.setSourceTypeStatus(!existingSourceType.getSourceTypeStatus());
		}
		existingSourceType.updateAuditHistory(auditFields);
		sourceTypeRepo.save(existingSourceType);
		return mapToSourceTypeResponse(existingSourceType);
	}

	@Override
	public void deleteSourceType(Long id) throws ResourceNotFoundException {
		SourceType sourceType = this.findSourceTypeById(id);
		sourceTypeRepo.deleteById(sourceType.getId());
	}

	@Override
	public void deleteBatchSourceType(List<Long> ids) throws ResourceNotFoundException {
		this.findAllSrcTypeById(ids);
		sourceTypeRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertSourceTypeListToMap(List<SourceType> sourceTypeList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (SourceType unit : sourceTypeList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getId());
			data.put("Name", unit.getSourceTypeName());
			data.put("Status", unit.getSourceTypeStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private SourceTypeResponse mapToSourceTypeResponse(SourceType sourceType) {
		return modelMapper.map(sourceType, SourceTypeResponse.class);
	}

	private SourceType findSourceTypeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<SourceType> sourceType = sourceTypeRepo.findById(id);
		if (sourceType.isEmpty()) {
			throw new ResourceNotFoundException("SourceType with ID " + id + " not found");
		}
		return sourceType.get();
	}

	private List<SourceType> findAllSrcTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<SourceType> sourceTypes = sourceTypeRepo.findAllById(ids);
		List<Long> missingIds = ids.stream()
				.filter(id -> sourceTypes.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Source Type with IDs " + missingIds + " not found.");
		}
		return sourceTypes;
	}

}
