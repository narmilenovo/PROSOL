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
import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.InspectionCode;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.InspectionCodeRepository;
import com.example.generalservice.service.interfaces.InspectionCodeService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionCodeServiceImpl implements InspectionCodeService {
	private final InspectionCodeRepository inspectionCodeRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(inspectionCodeRequest);
		String inCode = inspectionCodeRequest.getInCodeCode();
		String inName = inspectionCodeRequest.getInCodeName();
		boolean exists = inspectionCodeRepository.existsByInCodeCodeOrInCodeName(inCode, inName);
		if (!exists) {
			InspectionCode inspectionCode = modelMapper.map(inspectionCodeRequest, InspectionCode.class);
			for (Map.Entry<String, Object> entryField : inspectionCode.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = InspectionCode.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			InspectionCode savedInCode = inspectionCodeRepository.save(inspectionCode);
			return mapToCodeResponse(savedInCode);
		} else {
			throw new ResourceFoundException("Inspection Code Already Exist");
		}
	}

	@Override
	@Cacheable("inCode")
	public InspectionCodeResponse getInCodeById(Long id) throws ResourceNotFoundException {
		InspectionCode inspectionCode = this.findInCodeById(id);
		return mapToCodeResponse(inspectionCode);
	}

	@Override
	@Cacheable("inCode")
	public List<InspectionCodeResponse> getAllInCode() {
		List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAll();
		return inspectionCodes.stream().sorted(Comparator.comparing(InspectionCode::getId)).map(this::mapToCodeResponse)
				.toList();
	}

	@Override
	@Cacheable("inCode")
	public List<InspectionCodeResponse> findAllStatusTrue() {
		List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAllByInCodeStatusIsTrue();
		return inspectionCodes.stream().sorted(Comparator.comparing(InspectionCode::getId)).map(this::mapToCodeResponse)
				.toList();
	}

	@Override
	public InspectionCodeResponse updateInCode(Long id, InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
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
			return mapToCodeResponse(updatedInspectionCode);
		}
		throw new ResourceFoundException("Inspection code Already exist");
	}

	@Override
	public InspectionCodeResponse updateInCodeStatus(Long id) throws ResourceNotFoundException {
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
		return mapToCodeResponse(existingInspectionCode);
	}

	@Override
	public List<InspectionCodeResponse> updateBatchInCodeStatus(List<Long> ids) throws ResourceNotFoundException {
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
		return inspectionCodes.stream().map(this::mapToCodeResponse).toList();
	}

	@Override
	public void deleteInCodeId(Long id) throws ResourceNotFoundException {
		InspectionCode inspectionCode = this.findInCodeById(id);
		inspectionCodeRepository.deleteById(inspectionCode.getId());
	}

	@Override
	public void deleteBatchInCode(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		inspectionCodeRepository.deleteAllByIdInBatch(ids);
	}

	private InspectionCodeResponse mapToCodeResponse(InspectionCode inspectionCode) {
		return modelMapper.map(inspectionCode, InspectionCodeResponse.class);
	}

	private InspectionCode findInCodeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<InspectionCode> inspectionCode = inspectionCodeRepository.findById(id);
		if (inspectionCode.isEmpty()) {
			throw new ResourceNotFoundException("No Inspection found with this Id");
		}
		return inspectionCode.get();
	}

	private List<InspectionCode> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> inspectionCodes.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Inspection Code with IDs " + missingIds + " not found");
		}
		return inspectionCodes;
	}

}
