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
import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.UnitOfIssue;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.UnitOfIssueMapper;
import com.example.generalservice.repository.UnitOfIssueRepository;
import com.example.generalservice.service.interfaces.UnitOfIssueService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitOfIssueServiceImpl implements UnitOfIssueService {
	private final UnitOfIssueRepository unitOfIssueRepository;
	private final UnitOfIssueMapper unitOfIssueMapper;
	private final DynamicClient dynamicClient;

	@Override
	public UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(unitOfIssueRequest);
		String uoiCode = unitOfIssueRequest.getUoiCode();
		String uoiName = unitOfIssueRequest.getUoiName();
		if (unitOfIssueRepository.existsByUoiCodeOrUoiName(uoiCode, uoiName)) {
			throw new ResourceFoundException("Unit Of Issue is already exist");
		}
		UnitOfIssue unitOfIssue = unitOfIssueMapper.mapToUnitOfIssue(unitOfIssueRequest);
		validateDynamicFields(unitOfIssue);

		UnitOfIssue savedUnitOfIssue = unitOfIssueRepository.save(unitOfIssue);
		return unitOfIssueMapper.mapToUnitOfIssueResponse(savedUnitOfIssue);
	}

	@Override
	public UnitOfIssueResponse getUOIById(@NonNull Long id) throws ResourceNotFoundException {
		UnitOfIssue unitOfIssue = this.findUOIById(id);
		return unitOfIssueMapper.mapToUnitOfIssueResponse(unitOfIssue);
	}

	@Override
	public List<UnitOfIssueResponse> getAllUOI() {
		return unitOfIssueRepository.findAll().stream().sorted(Comparator.comparing(UnitOfIssue::getId))
				.map(unitOfIssueMapper::mapToUnitOfIssueResponse).toList();
	}

	@Override
	public List<UnitOfIssueResponse> findAllStatusTrue() {
		return unitOfIssueRepository.findAllByUoiStatusIsTrue().stream()
				.sorted(Comparator.comparing(UnitOfIssue::getId)).map(unitOfIssueMapper::mapToUnitOfIssueResponse)
				.toList();
	}

	@Override
	public UnitOfIssueResponse updateUOI(@NonNull Long id, UnitOfIssueRequest updateUnitOfIssueRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateUnitOfIssueRequest);
		String uoiCode = updateUnitOfIssueRequest.getUoiCode();
		String uoiName = updateUnitOfIssueRequest.getUoiName();
		UnitOfIssue existingUnitOfIssue = this.findUOIById(id);
		boolean exists = unitOfIssueRepository.existsByUoiCodeAndIdNotOrUoiNameAndIdNot(uoiCode, id, uoiName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingUnitOfIssue.getUoiCode().equals(uoiCode)) {
				auditFields.add(new AuditFields(null, "Uoi Code", existingUnitOfIssue.getUoiCode(), uoiCode));
				existingUnitOfIssue.setUoiCode(uoiCode);
			}
			if (!existingUnitOfIssue.getUoiName().equals(uoiName)) {
				auditFields.add(new AuditFields(null, "Uoi Name", existingUnitOfIssue.getUoiName(), uoiName));
				existingUnitOfIssue.setUoiName(uoiName);
			}
			if (!existingUnitOfIssue.getUoiStatus().equals(updateUnitOfIssueRequest.getUoiStatus())) {
				auditFields.add(new AuditFields(null, "Uoi Status", existingUnitOfIssue.getUoiStatus(),
						updateUnitOfIssueRequest.getUoiStatus()));
				existingUnitOfIssue.setUoiStatus(updateUnitOfIssueRequest.getUoiStatus());
			}
			if (!existingUnitOfIssue.getDynamicFields().equals(updateUnitOfIssueRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateUnitOfIssueRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingUnitOfIssue.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingUnitOfIssue.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingUnitOfIssue.updateAuditHistory(auditFields); // Update the audit history
			UnitOfIssue updatedUnitOfIssue = unitOfIssueRepository.save(existingUnitOfIssue);
			return unitOfIssueMapper.mapToUnitOfIssueResponse(updatedUnitOfIssue);
		}
		throw new ResourceFoundException("Unit Of Issue is already exist");
	}

	@Override
	public UnitOfIssueResponse updateUOIStatus(@NonNull Long id) throws ResourceNotFoundException {
		UnitOfIssue existingUnitOfIssue = this.findUOIById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingUnitOfIssue.getUoiStatus() != null) {
			auditFields.add(new AuditFields(null, "Uoi Status", existingUnitOfIssue.getUoiStatus(),
					!existingUnitOfIssue.getUoiStatus()));
			existingUnitOfIssue.setUoiStatus(!existingUnitOfIssue.getUoiStatus());
		}
		existingUnitOfIssue.updateAuditHistory(auditFields); // Update the audit history
		unitOfIssueRepository.save(existingUnitOfIssue);
		return unitOfIssueMapper.mapToUnitOfIssueResponse(existingUnitOfIssue);
	}

	@Override
	public List<UnitOfIssueResponse> updateBatchUOIStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<UnitOfIssue> unitOfIssues = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		unitOfIssues.forEach(existingUnitOfIssue -> {
			if (existingUnitOfIssue.getUoiStatus() != null) {
				auditFields.add(new AuditFields(null, "Uoi Status", existingUnitOfIssue.getUoiStatus(),
						!existingUnitOfIssue.getUoiStatus()));
				existingUnitOfIssue.setUoiStatus(!existingUnitOfIssue.getUoiStatus());
			}
			existingUnitOfIssue.updateAuditHistory(auditFields); // Update the audit history

		});
		unitOfIssueRepository.saveAll(unitOfIssues);
		return unitOfIssues.stream().map(unitOfIssueMapper::mapToUnitOfIssueResponse).toList();
	}

	@Override
	public void deleteUOIId(@NonNull Long id) throws ResourceNotFoundException {
		UnitOfIssue unitOfIssue = this.findUOIById(id);
		if (unitOfIssue != null) {
			unitOfIssueRepository.delete(unitOfIssue);
		}
	}

	@Override
	public void deleteBatchUOI(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<UnitOfIssue> unitOfIssues = this.findAllById(ids);
		if (!unitOfIssues.isEmpty()) {
			unitOfIssueRepository.deleteAll(unitOfIssues);
		}
	}

	private void validateDynamicFields(UnitOfIssue unitOfIssue) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : unitOfIssue.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = UnitOfIssue.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private UnitOfIssue findUOIById(@NonNull Long id) throws ResourceNotFoundException {
		return unitOfIssueRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Unit Of Issue found with this Id"));
	}

	private List<UnitOfIssue> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAllById(ids);

		Set<Long> foundIds = unitOfIssues.stream().map(UnitOfIssue::getId).collect(Collectors.toSet());

		List<Long> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Unit Of Issue with IDs " + missingIds + " not found");
		}

		return unitOfIssues;
	}

}
