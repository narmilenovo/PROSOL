package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.entity.AccAssignment;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.AccAssignmentRepository;
import com.example.sales_otherservice.service.interfaces.AccAssignmentService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccAssignmentServiceImpl implements AccAssignmentService {
	private final AccAssignmentRepository accAssignmentRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(accAssignmentRequest);
		String accCode = accAssignmentRequest.getAccCode();
		String accName = accAssignmentRequest.getAccName();
		boolean exists = accAssignmentRepository.existsByAccCodeOrAccName(accCode, accName);
		if (!exists) {

			AccAssignment accAssignment = modelMapper.map(accAssignmentRequest, AccAssignment.class);
			for (Map.Entry<String, Object> entryField : accAssignment.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AccAssignment.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			AccAssignment savedAssignment = accAssignmentRepository.save(accAssignment);
			return mapToAccAssignmentResponse(savedAssignment);
		}
		throw new ResourceFoundException("Acc assignment already exist");
	}

	@Override
	public List<AccAssignmentResponse> getAllAcc() {
		List<AccAssignment> accAssignments = accAssignmentRepository.findAll();
		return accAssignments.stream().sorted(Comparator.comparing(AccAssignment::getId))
				.map(this::mapToAccAssignmentResponse).toList();
	}

	@Override
	public AccAssignmentResponse getAccById(Long id) throws ResourceNotFoundException {
		AccAssignment accAssignment = this.findAccById(id);
		return mapToAccAssignmentResponse(accAssignment);
	}

	@Override
	public List<AccAssignmentResponse> findAllStatusTrue() {
		List<AccAssignment> list = accAssignmentRepository.findAllByAccStatusIsTrue();
		return list.stream().sorted(Comparator.comparing(AccAssignment::getId)).map(this::mapToAccAssignmentResponse)
				.toList();
	}

	@Override
	public AccAssignmentResponse updateAcc(Long id, AccAssignmentRequest updateAccAssignmentRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
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
			return mapToAccAssignmentResponse(updatedAssignment);
		}
		throw new ResourceFoundException("Acc assignment already exist");
	}

	@Override
	public AccAssignmentResponse updateAccStatus(Long id) throws ResourceNotFoundException {
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
		return mapToAccAssignmentResponse(existingAccAssignment);
	}

	@Override
	public List<AccAssignmentResponse> updateBatchAccStatus(List<Long> ids) throws ResourceNotFoundException {
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
				.map(this::mapToAccAssignmentResponse).toList();
	}

	@Override
	public void deleteAccId(Long id) throws ResourceNotFoundException {
		AccAssignment accAssignment = this.findAccById(id);
		accAssignmentRepository.deleteById(accAssignment.getId());
	}

	@Override
	public void deleteBatchAcc(List<Long> ids) throws ResourceNotFoundException {
		this.findAllAccById(ids);
		accAssignmentRepository.deleteAllByIdInBatch(ids);
	}

	private AccAssignmentResponse mapToAccAssignmentResponse(AccAssignment accAssignment) {
		return modelMapper.map(accAssignment, AccAssignmentResponse.class);
	}

	private AccAssignment findAccById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<AccAssignment> accAssignment = accAssignmentRepository.findById(id);
		if (accAssignment.isEmpty()) {
			throw new ResourceNotFoundException("Account Assignment not found with this Id");
		}
		return accAssignment.get();
	}

	private List<AccAssignment> findAllAccById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<AccAssignment> accAssignments = accAssignmentRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> accAssignments.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return accAssignments;
	}

}
