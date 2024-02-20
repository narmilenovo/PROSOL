package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.InspectionType;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.InspectionTypeRepository;
import com.example.generalservice.service.interfaces.InspectionTypeService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionTypeServiceImpl implements InspectionTypeService {
	private final InspectionTypeRepository inspectionTypeRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public InspectionTypeResponse saveInType(InspectionTypeRequest inspectionTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(inspectionTypeRequest);
		String inTypeCode = inspectionTypeRequest.getInTypeCode();
		String inTypeName = inspectionTypeRequest.getInTypeName();
		boolean exists = inspectionTypeRepository.existsByInTypeCodeOrInTypeName(inTypeCode, inTypeName);
		if (!exists) {

			InspectionType inspectionType = modelMapper.map(inspectionTypeRequest, InspectionType.class);
			for (Map.Entry<String, Object> entryField : inspectionType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = InspectionType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			InspectionType savedInspectionType = inspectionTypeRepository.save(inspectionType);
			return mapToInspectionTypeResponse(savedInspectionType);
		}
		throw new ResourceFoundException("Inspection type Already exist");

	}

	@Override
	@Cacheable("inType")
	public InspectionTypeResponse getInTypeById(Long id) throws ResourceNotFoundException {
		InspectionType inspectionType = this.findInTypeById(id);
		return mapToInspectionTypeResponse(inspectionType);
	}

	@Override
	@Cacheable("inType")
	public List<InspectionTypeResponse> getAllInType() {
		List<InspectionType> inspectionTypes = inspectionTypeRepository.findAll();
		return inspectionTypes.stream().sorted(Comparator.comparing(InspectionType::getId))
				.map(this::mapToInspectionTypeResponse).toList();
	}

	@Override
	@Cacheable("inType")
	public List<InspectionTypeResponse> findAllStatusTrue() {
		List<InspectionType> inspectionTypes = inspectionTypeRepository.findAllByInTypeStatusIsTrue();
		return inspectionTypes.stream().sorted(Comparator.comparing(InspectionType::getId))
				.map(this::mapToInspectionTypeResponse).toList();
	}

	@Override
	public InspectionTypeResponse updateInType(Long id, InspectionTypeRequest updateInspectionTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateInspectionTypeRequest);
		String inTypeCode = updateInspectionTypeRequest.getInTypeCode();
		String inTypeName = updateInspectionTypeRequest.getInTypeName();
		InspectionType existingInspectionType = this.findInTypeById(id);
		boolean exists = inspectionTypeRepository.existsByInTypeCodeAndIdNotOrInTypeNameAndIdNot(inTypeCode, id,
				inTypeName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingInspectionType.getInTypeCode().equals(inTypeCode)) {
				auditFields
						.add(new AuditFields(null, "InType Code", existingInspectionType.getInTypeCode(), inTypeCode));
				existingInspectionType.setInTypeCode(inTypeCode);
			}
			if (!existingInspectionType.getInTypeName().equals(inTypeName)) {
				auditFields
						.add(new AuditFields(null, "InType Name", existingInspectionType.getInTypeName(), inTypeName));
				existingInspectionType.setInTypeName(inTypeName);
			}
			if (!existingInspectionType.getInTypeStatus().equals(updateInspectionTypeRequest.getInTypeStatus())) {
				auditFields.add(new AuditFields(null, "InType Status", existingInspectionType.getInTypeStatus(),
						updateInspectionTypeRequest.getInTypeStatus()));
				existingInspectionType.setInTypeStatus(updateInspectionTypeRequest.getInTypeStatus());
			}
			if (!existingInspectionType.getDynamicFields().equals(updateInspectionTypeRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateInspectionTypeRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingInspectionType.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingInspectionType.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingInspectionType.updateAuditHistory(auditFields);
			InspectionType updatedInspectionType = inspectionTypeRepository.save(existingInspectionType);
			return mapToInspectionTypeResponse(updatedInspectionType);
		}
		throw new ResourceFoundException("Inspection type Already exist");
	}

	@Override
	public InspectionTypeResponse updateInTypeStatus(Long id) throws ResourceNotFoundException {
		InspectionType existingInspectionType = this.findInTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingInspectionType.getInTypeStatus() != null) {
			auditFields.add(new AuditFields(null, "InType Status", existingInspectionType.getInTypeStatus(),
					!existingInspectionType.getInTypeStatus()));
			existingInspectionType.setInTypeStatus(!existingInspectionType.getInTypeStatus());
		}
		existingInspectionType.updateAuditHistory(auditFields);
		inspectionTypeRepository.save(existingInspectionType);
		return this.mapToInspectionTypeResponse(existingInspectionType);
	}

	@Override
	public List<InspectionTypeResponse> updateBatchInTypeStatus(List<Long> ids) throws ResourceNotFoundException {
		List<InspectionType> inspectionTypes = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		inspectionTypes.forEach(existingInspectionType -> {
			if (existingInspectionType.getInTypeStatus() != null) {
				auditFields.add(new AuditFields(null, "InType Status", existingInspectionType.getInTypeStatus(),
						!existingInspectionType.getInTypeStatus()));
				existingInspectionType.setInTypeStatus(!existingInspectionType.getInTypeStatus());
			}
			existingInspectionType.updateAuditHistory(auditFields);

		});
		inspectionTypeRepository.saveAll(inspectionTypes);
		return inspectionTypes.stream().map(this::mapToInspectionTypeResponse).toList();
	}

	@Override
	public void deleteInTypeId(Long id) throws ResourceNotFoundException {
		InspectionType inspectionType = this.findInTypeById(id);
		inspectionTypeRepository.deleteById(inspectionType.getId());
	}

	@Override
	public void deleteBatchInType(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		inspectionTypeRepository.deleteAllById(ids);
	}

	private InspectionTypeResponse mapToInspectionTypeResponse(InspectionType inspectionType) {
		return modelMapper.map(inspectionType, InspectionTypeResponse.class);
	}

	private InspectionType findInTypeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<InspectionType> inspectionType = inspectionTypeRepository.findById(id);
		if (inspectionType.isEmpty()) {
			throw new ResourceNotFoundException("No Inspection found with this Id");
		}
		return inspectionType.get();
	}

	private List<InspectionType> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<InspectionType> inspectionTypes = inspectionTypeRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> inspectionTypes.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Inspection Type with IDs " + missingIds + " not found");
		}
		return inspectionTypes;
	}

}
