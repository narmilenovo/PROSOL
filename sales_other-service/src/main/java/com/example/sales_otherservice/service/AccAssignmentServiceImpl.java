package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.entity.AccAssignment;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.AccAssignmentMapper;
import com.example.sales_otherservice.repository.AccAssignmentRepository;
import com.example.sales_otherservice.service.interfaces.AccAssignmentService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccAssignmentServiceImpl implements AccAssignmentService {
	private final AccAssignmentRepository accAssignmentRepository;
	private final AccAssignmentMapper accAssignmentMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(accAssignmentRequest);
		String accCode = accAssignmentRequest.getAccCode();
		String accName = accAssignmentRequest.getAccName();
		if (accAssignmentRepository.existsByAccCodeOrAccName(accCode, accName)) {
			throw new ResourceFoundException("Acc assignment already exist");
		}

		AccAssignment accAssignment = accAssignmentMapper.mapToAccAssignment(accAssignmentRequest);

		validateDynamicFields(accAssignment);

		AccAssignment savedAssignment = accAssignmentRepository.save(accAssignment);
		return accAssignmentMapper.mapToAccAssignmentResponse(savedAssignment);
	}

	@Override
	public List<AccAssignmentResponse> getAllAcc() {
		return accAssignmentRepository.findAll().stream().sorted(Comparator.comparing(AccAssignment::getId))
				.map(accAssignmentMapper::mapToAccAssignmentResponse).toList();
	}

	@Override
	public AccAssignmentResponse getAccById(@NonNull Long id) throws ResourceNotFoundException {
		AccAssignment accAssignment = this.findAccById(id);
		return accAssignmentMapper.mapToAccAssignmentResponse(accAssignment);
	}

	@Override
	public List<AccAssignmentResponse> findAllStatusTrue() {
		return accAssignmentRepository.findAllByAccStatusIsTrue().stream()
				.sorted(Comparator.comparing(AccAssignment::getId)).map(accAssignmentMapper::mapToAccAssignmentResponse)
				.toList();
	}

	@Override
	public AccAssignmentResponse updateAcc(@NonNull Long id, AccAssignmentRequest updateAccAssignmentRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateAccAssignmentRequest);
		AccAssignment existingAssignment = this.findAccById(id);
		String accCode = updateAccAssignmentRequest.getAccCode();
		String accName = updateAccAssignmentRequest.getAccName();
		boolean exists = accAssignmentRepository.existsByAccCodeAndIdNotOrAccNameAndIdNot(accCode, id, accName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingAssignment.getAccCode().equals(accCode)) {
				auditFields.add(new AuditFields(null, "Acc Code", existingAssignment.getAccCode(), accCode));
				existingAssignment.setAccCode(accCode);
			}
			if (!existingAssignment.getAccName().equals(accName)) {
				auditFields.add(new AuditFields(null, "Acc Name", existingAssignment.getAccName(), accName));
				existingAssignment.setAccName(accName);
			}
			if (!existingAssignment.getAccStatus().equals(updateAccAssignmentRequest.getAccStatus())) {
				auditFields.add(new AuditFields(null, "Acc Status", existingAssignment.getAccStatus(),
						updateAccAssignmentRequest.getAccStatus()));
				existingAssignment.setAccStatus(updateAccAssignmentRequest.getAccStatus());
			}
			if (!existingAssignment.getDynamicFields().equals(updateAccAssignmentRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateAccAssignmentRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingAssignment.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingAssignment.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}

			AccAssignment updatedAssignment = accAssignmentRepository.save(existingAssignment);
			return accAssignmentMapper.mapToAccAssignmentResponse(updatedAssignment);
		}
		throw new ResourceFoundException("Acc assignment already exist");
	}

	@Override
	public AccAssignmentResponse updateAccStatus(@NonNull Long id) throws ResourceNotFoundException {
		AccAssignment existingAccAssignment = this.findAccById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingAccAssignment.getAccStatus() != null) {
			auditFields.add(new AuditFields(null, "Acc Status", existingAccAssignment.getAccStatus(),
					!existingAccAssignment.getAccStatus()));
			existingAccAssignment.setAccStatus(!existingAccAssignment.getAccStatus());
		}
		existingAccAssignment.updateAuditHistory(auditFields);
		accAssignmentRepository.save(existingAccAssignment);
		return accAssignmentMapper.mapToAccAssignmentResponse(existingAccAssignment);
	}

	@Override
	public List<AccAssignmentResponse> updateBatchAccStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<AccAssignment> accAssignments = this.findAllAccById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		accAssignments.forEach(existingAccAssignment -> {
			if (existingAccAssignment.getAccStatus() != null) {
				auditFields.add(new AuditFields(null, "Acc Status", existingAccAssignment.getAccStatus(),
						!existingAccAssignment.getAccStatus()));
				existingAccAssignment.setAccStatus(!existingAccAssignment.getAccStatus());
			}
			existingAccAssignment.updateAuditHistory(auditFields);
		});
		accAssignmentRepository.saveAll(accAssignments);
		return accAssignments.stream().sorted(Comparator.comparing(AccAssignment::getId))
				.map(accAssignmentMapper::mapToAccAssignmentResponse).toList();
	}

	@Override
	public void deleteAccId(@NonNull Long id) throws ResourceNotFoundException {
		AccAssignment accAssignment = this.findAccById(id);
		if (accAssignment != null) {
			accAssignmentRepository.delete(accAssignment);
		}
	}

	@Override
	public void deleteBatchAcc(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<AccAssignment> accAssignments = this.findAllAccById(ids);
		if (!accAssignments.isEmpty()) {
			accAssignmentRepository.deleteAll(accAssignments);
		}
	}

	private void validateDynamicFields(AccAssignment accAssignment) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : accAssignment.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = AccAssignment.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private AccAssignment findAccById(@NonNull Long id) throws ResourceNotFoundException {
		return accAssignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Account Assignment not found with this Id"));
	}

	private List<AccAssignment> findAllAccById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<AccAssignment> accAssignments = accAssignmentRepository.findAllById(ids);

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("AccAssignment with IDs " + missingIds + " not found.");
		}

		return accAssignments;
	}

}
