package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.InspectionCode;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.InspectionCodeMapper;
import com.example.generalservice.repository.InspectionCodeRepository;
import com.example.generalservice.service.interfaces.InspectionCodeService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionCodeServiceImpl implements InspectionCodeService {
	private final InspectionCodeRepository inspectionCodeRepository;
	private final InspectionCodeMapper inspectionCodeMapper;
	private final DynamicClient dynamicClient;

	@Override
	public InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(inspectionCodeRequest);
		String inCode = inspectionCodeRequest.getInCodeCode();
		String inName = inspectionCodeRequest.getInCodeName();
		if (inspectionCodeRepository.existsByInCodeCodeOrInCodeName(inCode, inName)) {
			throw new ResourceFoundException("Inspection Code Already Exist");
		}
		InspectionCode inspectionCode = inspectionCodeMapper.mapToInspectionCode(inspectionCodeRequest);
		validateDynamicFields(inspectionCode);

		InspectionCode savedInCode = inspectionCodeRepository.save(inspectionCode);
		return inspectionCodeMapper.mapToCodeResponse(savedInCode);
	}

	@Override
	public InspectionCodeResponse getInCodeById(@NonNull Long id) throws ResourceNotFoundException {
		InspectionCode inspectionCode = this.findInCodeById(id);
		return inspectionCodeMapper.mapToCodeResponse(inspectionCode);
	}

	@Override
	public List<InspectionCodeResponse> getAllInCode() {
		return inspectionCodeRepository.findAll().stream().sorted(Comparator.comparing(InspectionCode::getId))
				.map(inspectionCodeMapper::mapToCodeResponse).toList();
	}

	@Override
	public List<InspectionCodeResponse> findAllStatusTrue() {
		return inspectionCodeRepository.findAllByInCodeStatusIsTrue().stream()
				.sorted(Comparator.comparing(InspectionCode::getId)).map(inspectionCodeMapper::mapToCodeResponse)
				.toList();
	}

	@Override
	public InspectionCodeResponse updateInCode(@NonNull Long id, InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateInspectionCodeRequest);
		String inCodeCode = updateInspectionCodeRequest.getInCodeCode();
		String inCodeName = updateInspectionCodeRequest.getInCodeName();
		InspectionCode existingInspectionCode = this.findInCodeById(id);
		boolean exists = inspectionCodeRepository.existsByInCodeCodeAndIdNotOrInCodeNameAndIdNot(inCodeCode, id,
				inCodeName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingInspectionCode.getInCodeCode().equals(inCodeCode)) {
				auditFields
						.add(new AuditFields(null, "InCode Code", existingInspectionCode.getInCodeCode(), inCodeCode));
				existingInspectionCode.setInCodeCode(inCodeCode);
			}
			if (!existingInspectionCode.getInCodeName().equals(inCodeName)) {
				auditFields
						.add(new AuditFields(null, "InCode Name", existingInspectionCode.getInCodeName(), inCodeName));
				existingInspectionCode.setInCodeName(inCodeName);
			}
			if (!existingInspectionCode.getInCodeStatus().equals(updateInspectionCodeRequest.getInCodeStatus())) {
				auditFields.add(new AuditFields(null, "InCode Status", existingInspectionCode.getInCodeStatus(),
						updateInspectionCodeRequest.getInCodeStatus()));
				existingInspectionCode.setInCodeStatus(updateInspectionCodeRequest.getInCodeStatus());
			}
			if (!existingInspectionCode.getDynamicFields().equals(updateInspectionCodeRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateInspectionCodeRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingInspectionCode.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingInspectionCode.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingInspectionCode.updateAuditHistory(auditFields);
			InspectionCode updatedInspectionCode = inspectionCodeRepository.save(existingInspectionCode);
			return inspectionCodeMapper.mapToCodeResponse(updatedInspectionCode);
		}
		throw new ResourceFoundException("Inspection code Already exist");
	}

	@Override
	public InspectionCodeResponse updateInCodeStatus(@NonNull Long id) throws ResourceNotFoundException {
		InspectionCode existingInspectionCode = this.findInCodeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingInspectionCode.getInCodeStatus() != null) {
			auditFields.add(new AuditFields(null, "InCode Status", existingInspectionCode.getInCodeStatus(),
					!existingInspectionCode.getInCodeStatus()));
			existingInspectionCode.setInCodeStatus(!existingInspectionCode.getInCodeStatus());
		}
		existingInspectionCode.updateAuditHistory(auditFields);
		inspectionCodeRepository.save(existingInspectionCode);
		return inspectionCodeMapper.mapToCodeResponse(existingInspectionCode);
	}

	@Override
	public List<InspectionCodeResponse> updateBatchInCodeStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<InspectionCode> inspectionCodes = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		inspectionCodes.forEach(existingInspectionCode -> {
			if (existingInspectionCode.getInCodeStatus() != null) {
				auditFields.add(new AuditFields(null, "InCode Status", existingInspectionCode.getInCodeStatus(),
						!existingInspectionCode.getInCodeStatus()));
				existingInspectionCode.setInCodeStatus(!existingInspectionCode.getInCodeStatus());
			}
			existingInspectionCode.updateAuditHistory(auditFields);

		});
		inspectionCodeRepository.saveAll(inspectionCodes);
		return inspectionCodes.stream().map(inspectionCodeMapper::mapToCodeResponse).toList();
	}

	@Override
	public void deleteInCodeId(@NonNull Long id) throws ResourceNotFoundException {
		InspectionCode inspectionCode = this.findInCodeById(id);
		if (inspectionCode != null) {
			inspectionCodeRepository.delete(inspectionCode);
		}
	}

	@Override
	public void deleteBatchInCode(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<InspectionCode> inspectionCodes = this.findAllById(ids);
		if (!inspectionCodes.isEmpty()) {
			inspectionCodeRepository.deleteAll(inspectionCodes);
		}
	}

	private void validateDynamicFields(InspectionCode inspectionCode) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : inspectionCode.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = InspectionCode.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}

	}

	private InspectionCode findInCodeById(@NonNull Long id) throws ResourceNotFoundException {
		return inspectionCodeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Inspection found with this Id"));
	}

	private List<InspectionCode> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAllById(ids);
		Set<Long> foundIds = inspectionCodes.stream().map(InspectionCode::getId).collect(Collectors.toSet());
		List<Long> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).toList();
		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Inspection Code with IDs " + missingIds + " not found");
		}
		return inspectionCodes;
	}

}
