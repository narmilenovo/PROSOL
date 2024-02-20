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
import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.UnitOfIssue;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.UnitOfIssueRepository;
import com.example.generalservice.service.interfaces.UnitOfIssueService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitOfIssueServiceImpl implements UnitOfIssueService {
	private final UnitOfIssueRepository unitOfIssueRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(unitOfIssueRequest);
		String uoiCode = unitOfIssueRequest.getUoiCode();
		String uoiName = unitOfIssueRequest.getUoiName();
		boolean exists = unitOfIssueRepository.existsByUoiCodeOrUoiName(uoiCode, uoiName);
		if (!exists) {

			UnitOfIssue unitOfIssue = modelMapper.map(unitOfIssueRequest, UnitOfIssue.class);
			for (Map.Entry<String, Object> entryField : unitOfIssue.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = UnitOfIssue.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			UnitOfIssue savedUnitOfIssue = unitOfIssueRepository.save(unitOfIssue);
			return mapToUnitOfIssueResponse(savedUnitOfIssue);
		}
		throw new ResourceFoundException("Unit Of Issue is already exist");
	}

	@Override
	@Cacheable("uoi")
	public UnitOfIssueResponse getUOIById(Long id) throws ResourceNotFoundException {
		UnitOfIssue unitOfIssue = this.findUOIById(id);
		return mapToUnitOfIssueResponse(unitOfIssue);
	}

	@Override
	@Cacheable("uoi")
	public List<UnitOfIssueResponse> getAllUOI() {
		List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAll();
		return unitOfIssues.stream().sorted(Comparator.comparing(UnitOfIssue::getId))
				.map(this::mapToUnitOfIssueResponse).toList();
	}

	@Override
	@Cacheable("uoi")
	public List<UnitOfIssueResponse> findAllStatusTrue() {
		List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAllByUoiStatusIsTrue();
		return unitOfIssues.stream().sorted(Comparator.comparing(UnitOfIssue::getId))
				.map(this::mapToUnitOfIssueResponse).toList();
	}

	@Override
	public UnitOfIssueResponse updateUOI(Long id, UnitOfIssueRequest updateUnitOfIssueRequest)
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
			return mapToUnitOfIssueResponse(updatedUnitOfIssue);
		}
		throw new ResourceFoundException("Unit Of Issue is already exist");
	}

	@Override
	public UnitOfIssueResponse updateUOIStatus(Long id) throws ResourceNotFoundException {
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
		return this.mapToUnitOfIssueResponse(existingUnitOfIssue);
	}

	@Override
	public List<UnitOfIssueResponse> updateBatchUOIStatus(List<Long> ids) throws ResourceNotFoundException {
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
		return unitOfIssues.stream().map(this::mapToUnitOfIssueResponse).toList();
	}

	@Override
	public void deleteUOIId(Long id) throws ResourceNotFoundException {
		UnitOfIssue unitOfIssue = this.findUOIById(id);
		unitOfIssueRepository.deleteById(unitOfIssue.getId());
	}

	@Override
	public void deleteBatchUOI(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		unitOfIssueRepository.deleteAllById(ids);
	}

	private UnitOfIssueResponse mapToUnitOfIssueResponse(UnitOfIssue unitOfIssue) {
		return modelMapper.map(unitOfIssue, UnitOfIssueResponse.class);
	}

	private UnitOfIssue findUOIById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<UnitOfIssue> unitOfIssue = unitOfIssueRepository.findById(id);
		if (unitOfIssue.isEmpty()) {
			throw new ResourceNotFoundException("No Unit Of Issue found with this Id");
		}
		return unitOfIssue.get();
	}

	private List<UnitOfIssue> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> unitOfIssues.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Unit Of Issue with IDs " + missingIds + " not found");
		}
		return unitOfIssues;
	}

}
