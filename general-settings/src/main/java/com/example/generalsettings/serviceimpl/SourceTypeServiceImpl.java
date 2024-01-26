package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

	public static final String SOURCE_TYPE_NOT_FOUND_MESSAGE = null;

	@Override
	public SourceTypeResponse saveSourceType(SourceTypeRequest sourceTypeRequest) throws AlreadyExistsException {
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
		String name = sourceTypeRequest.getSourceTypeName();
		String code = sourceTypeRequest.getSourceTypeCode();
		boolean exists = sourceTypeRepo.existsBySourceTypeCodeAndSourceTypeNameAndIdNot(code, name, id);
		if (!exists) {
			SourceType existingSourceType = this.findSourceTypeById(id);
			modelMapper.map(sourceTypeRequest, existingSourceType);
			sourceTypeRepo.save(existingSourceType);
			return mapToSourceTypeResponse(existingSourceType);
		} else {
			throw new AlreadyExistsException("SourceType with this name already exists");
		}
	}

	@Override
	public List<SourceTypeResponse> updateBulkStatusSourceTypeId(List<Long> id) throws ResourceNotFoundException {
		List<SourceType> existingSourceType = this.findAllSrcTypeById(id);
		for (SourceType sourceType : existingSourceType) {
			sourceType.setSourceTypeStatus(!sourceType.getSourceTypeStatus());
		}
		sourceTypeRepo.saveAll(existingSourceType);
		return existingSourceType.stream().map(this::mapToSourceTypeResponse).toList();
	}

	@Override
	public SourceTypeResponse updateStatusUsingSourceTypeId(Long id) throws ResourceNotFoundException {
		SourceType existingSourceType = this.findSourceTypeById(id);
		existingSourceType.setSourceTypeStatus(!existingSourceType.getSourceTypeStatus());
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
			throw new ResourceNotFoundException(SOURCE_TYPE_NOT_FOUND_MESSAGE);
		}
		return sourceType.get();
	}

	private List<SourceType> findAllSrcTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<SourceType> sourceTypes = sourceTypeRepo.findAllById(ids);
		List<Long> missingIds = ids.stream()
				.filter(id -> sourceTypes.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Source Type with IDs " + missingIds + " not found.");
		}
		return sourceTypes;
	}

}
