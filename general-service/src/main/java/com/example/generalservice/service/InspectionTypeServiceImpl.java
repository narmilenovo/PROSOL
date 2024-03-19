package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.InspectionType;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.InspectionTypeMapper;
import com.example.generalservice.repository.InspectionTypeRepository;
import com.example.generalservice.service.interfaces.InspectionTypeService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionTypeServiceImpl implements InspectionTypeService {
	private final InspectionTypeRepository inspectionTypeRepository;
	private final InspectionTypeMapper inspectionTypeMapper;
	private final DynamicClient dynamicClient;

	@Override
	public InspectionTypeResponse saveInType(InspectionTypeRequest inspectionTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(inspectionTypeRequest);
		String inTypeCode = inspectionTypeRequest.getInTypeCode();
		String inTypeName = inspectionTypeRequest.getInTypeName();
		if (inspectionTypeRepository.existsByInTypeCodeOrInTypeName(inTypeCode, inTypeName)) {
			throw new ResourceFoundException("Inspection type Already exist");
		}
		InspectionType inspectionType = inspectionTypeMapper.mapToInspectionType(inspectionTypeRequest);
		validateDynamicFields(inspectionType);

		InspectionType savedInspectionType = inspectionTypeRepository.save(inspectionType);
		return inspectionTypeMapper.mapToInspectionTypeResponse(savedInspectionType);

	}

	@Override
	public InspectionTypeResponse getInTypeById(@NonNull Long id) throws ResourceNotFoundException {
		InspectionType inspectionType = this.findInTypeById(id);
		return inspectionTypeMapper.mapToInspectionTypeResponse(inspectionType);
	}

	@Override
	public List<InspectionTypeResponse> getAllInType() {
		return inspectionTypeRepository.findAll().stream().sorted(Comparator.comparing(InspectionType::getId))
				.map(inspectionTypeMapper::mapToInspectionTypeResponse).toList();
	}

	@Override
	public List<InspectionTypeResponse> findAllStatusTrue() {
		return inspectionTypeRepository.findAllByInTypeStatusIsTrue().stream()
				.sorted(Comparator.comparing(InspectionType::getId))
				.map(inspectionTypeMapper::mapToInspectionTypeResponse).toList();
	}

	@Override
	public InspectionTypeResponse updateInType(@NonNull Long id, InspectionTypeRequest updateInspectionTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
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
			return inspectionTypeMapper.mapToInspectionTypeResponse(updatedInspectionType);
		}
		throw new ResourceFoundException("Inspection type Already exist");
	}

	@Override
	public InspectionTypeResponse updateInTypeStatus(@NonNull Long id) throws ResourceNotFoundException {
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
		return inspectionTypeMapper.mapToInspectionTypeResponse(existingInspectionType);
	}

	@Override
	public List<InspectionTypeResponse> updateBatchInTypeStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
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
		return inspectionTypes.stream().map(inspectionTypeMapper::mapToInspectionTypeResponse).toList();
	}

	@Override
	public void deleteInTypeId(@NonNull Long id) throws ResourceNotFoundException {
		InspectionType inspectionType = this.findInTypeById(id);
		if (inspectionType != null) {
			inspectionTypeRepository.delete(inspectionType);
		}
	}

	@Override
	public void deleteBatchInType(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<InspectionType> inspectionTypes = this.findAllById(ids);
		if (!inspectionTypes.isEmpty()) {
			inspectionTypeRepository.deleteAll(inspectionTypes);
		}
	}

	private void validateDynamicFields(InspectionType inspectionType) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : inspectionType.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = InspectionType.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private InspectionType findInTypeById(@NonNull Long id) throws ResourceNotFoundException {
		return inspectionTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Inspection found with this Id"));
	}

	private List<InspectionType> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
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
