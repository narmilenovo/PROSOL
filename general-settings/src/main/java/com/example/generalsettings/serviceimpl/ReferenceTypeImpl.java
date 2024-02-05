package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

	public static final String REFERENCE_TYPE_NOT_FOUND_MESSAGE = null;

	@Override
	public ReferenceTypeResponse saveReferenceType(ReferenceTypeRequest referenceTypeRequest)
			throws AlreadyExistsException {
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
		String name = referenceTypeRequest.getReferenceTypeName();
		String code = referenceTypeRequest.getReferenceTypeCode();
		boolean exists = referenceTypeRepo.existsByReferenceTypeCodeAndReferenceTypeNameAndIdNot(code, name, id);
		if (!exists) {
			ReferenceType existingReferenceType = this.findReferenceTypeById(id);
			modelMapper.map(referenceTypeRequest, existingReferenceType);
			referenceTypeRepo.save(existingReferenceType);
			return mapToReferenceTypeResponse(existingReferenceType);
		} else {
			throw new AlreadyExistsException("ReferenceType with this name already exists");
		}
	}

	@Override
	public List<ReferenceTypeResponse> updateBulkStatusReferenceTypeId(List<Long> id) throws ResourceNotFoundException {
		List<ReferenceType> existingReferenceType = this.findAllRefTypeById(id);
		for (ReferenceType referenceType : existingReferenceType) {
			referenceType.setReferenceTypeStatus(!referenceType.getReferenceTypeStatus());
		}
		referenceTypeRepo.saveAll(existingReferenceType);
		return existingReferenceType.stream().map(this::mapToReferenceTypeResponse).toList();
	}

	@Override
	public ReferenceTypeResponse updateStatusUsingReferenceTypeId(Long id) throws ResourceNotFoundException {
		ReferenceType existingReferenceType = this.findReferenceTypeById(id);
		existingReferenceType.setReferenceTypeStatus(!existingReferenceType.getReferenceTypeStatus());
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
			throw new ResourceNotFoundException(REFERENCE_TYPE_NOT_FOUND_MESSAGE);
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
