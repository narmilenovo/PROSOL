package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.SourceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.SourceTypeMapper;
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

	private final SourceTypeMapper sourceTypeMapper;

	@Override
	public SourceTypeResponse saveSourceType(SourceTypeRequest sourceTypeRequest) throws AlreadyExistsException {
		Helpers.inputTitleCase(sourceTypeRequest);
		String sourceTypeCode = sourceTypeRequest.getSourceTypeCode();
		String sourceTypeName = sourceTypeRequest.getSourceTypeName();
		if (sourceTypeRepo.existsBySourceTypeCodeAndSourceTypeName(sourceTypeCode, sourceTypeName)) {
			throw new AlreadyExistsException("SourceType with this name already exists");
		}
		SourceType sourceType = sourceTypeMapper.mapToSourceType(sourceTypeRequest);
		sourceTypeRepo.save(sourceType);
		return sourceTypeMapper.mapToSourceTypeResponse(sourceType);
	}

	@Override
	public SourceTypeResponse getSourceTypeById(Long id) throws ResourceNotFoundException {
		SourceType sourceType = this.findSourceTypeById(id);
		return sourceTypeMapper.mapToSourceTypeResponse(sourceType);
	}

	@Override
	public List<SourceTypeResponse> getAllSourceType() {
		return sourceTypeRepo.findAllByOrderByIdAsc().stream().map(sourceTypeMapper::mapToSourceTypeResponse).toList();
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
			return sourceTypeMapper.mapToSourceTypeResponse(existingSourceType);
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
		return existingSourceTypeList.stream().map(sourceTypeMapper::mapToSourceTypeResponse).toList();
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
		return sourceTypeMapper.mapToSourceTypeResponse(existingSourceType);
	}

	@Override
	public void deleteSourceType(Long id) throws ResourceNotFoundException {
		SourceType sourceType = this.findSourceTypeById(id);
		if (sourceType != null) {
			sourceTypeRepo.delete(sourceType);
		}
	}

	@Override
	public void deleteBatchSourceType(List<Long> ids) throws ResourceNotFoundException {
		List<SourceType> sourceTypes = this.findAllSrcTypeById(ids);
		if (!sourceTypes.isEmpty()) {
			sourceTypeRepo.deleteAll(sourceTypes);
		}
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

	private SourceType findSourceTypeById(Long id) throws ResourceNotFoundException {
		return sourceTypeRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("SourceType with ID " + id + " not found"));
	}

	private List<SourceType> findAllSrcTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);

		List<SourceType> sourceTypes = sourceTypeRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);

		List<SourceType> foundTypes = sourceTypes.stream().filter(type -> idSet.contains(type.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Source Type with IDs " + missingIds + " not found.");
		}

		return foundTypes;
	}

}
