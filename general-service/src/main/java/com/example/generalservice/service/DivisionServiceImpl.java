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
import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.Division;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.DivisionMapper;
import com.example.generalservice.repository.DivisionRepository;
import com.example.generalservice.service.interfaces.DivisionService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {
	private final DivisionRepository divisionRepository;
	private final DivisionMapper divisionMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DivisionResponse saveDivision(DivisionRequest divisionRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(divisionRequest);
		String divCode = divisionRequest.getDivCode();
		String divName = divisionRequest.getDivName();
		if (divisionRepository.existsByDivCodeOrDivName(divCode, divName)) {
			throw new ResourceFoundException("Division Already Exist");
		}
		Division division = divisionMapper.mapToDivision(divisionRequest);
		validateDynamicFields(division);

		Division savedDivision = divisionRepository.save(division);
		return divisionMapper.mapToDivisionResponse(savedDivision);
	}

	@Override
	public DivisionResponse getDivisionById(@NonNull Long id) throws ResourceNotFoundException {
		Division division = this.findDivisionById(id);
		return divisionMapper.mapToDivisionResponse(division);
	}

	@Override
	public List<DivisionResponse> getAllDivision() {
		return divisionRepository.findAll().stream().sorted(Comparator.comparing(Division::getId))
				.map(divisionMapper::mapToDivisionResponse).toList();
	}

	@Override
	public List<DivisionResponse> findAllStatusTrue() {
		return divisionRepository.findAllByDivStatusIsTrue().stream().sorted(Comparator.comparing(Division::getId))
				.map(divisionMapper::mapToDivisionResponse).toList();
	}

	@Override
	public DivisionResponse updateDivision(@NonNull Long id, DivisionRequest updateDivisionRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateDivisionRequest);
		String divCode = updateDivisionRequest.getDivCode();
		String divName = updateDivisionRequest.getDivName();
		Division existingDivision = this.findDivisionById(id);
		boolean exists = divisionRepository.existsByDivCodeAndIdNotOrDivNameAndIdNot(divCode, id, divName, id);
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingDivision.getDivCode().equals(divCode)) {
				auditFields.add(new AuditFields(null, "Div Code", existingDivision.getDivCode(), divCode));
				existingDivision.setDivCode(divCode);
			}
			if (!existingDivision.getDivName().equals(divName)) {
				auditFields.add(new AuditFields(null, "Div Name", existingDivision.getDivName(), divName));
				existingDivision.setDivName(divName);
			}
			if (!existingDivision.getDivStatus().equals(updateDivisionRequest.getDivStatus())) {
				auditFields.add(new AuditFields(null, "Div Status", existingDivision.getDivStatus(),
						updateDivisionRequest.getDivStatus()));
				existingDivision.setDivStatus(updateDivisionRequest.getDivStatus());
			}
			if (!existingDivision.getDynamicFields().equals(updateDivisionRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateDivisionRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingDivision.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingDivision.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingDivision.updateAuditHistory(auditFields);
			Division updatedDivision = divisionRepository.save(existingDivision);
			return divisionMapper.mapToDivisionResponse(updatedDivision);
		}
		throw new ResourceFoundException("Division Already Exist");
	}

	@Override
	public DivisionResponse updateDivisionStatus(@NonNull Long id) throws ResourceNotFoundException {
		Division existingDivision = this.findDivisionById(id);
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingDivision.getDivStatus() != null) {
			auditFields.add(new AuditFields(null, "Div Status", existingDivision.getDivStatus(),
					!existingDivision.getDivStatus()));
			existingDivision.setDivStatus(!existingDivision.getDivStatus());
		}
		existingDivision.updateAuditHistory(auditFields);
		divisionRepository.save(existingDivision);
		return divisionMapper.mapToDivisionResponse(existingDivision);
	}

	@Override
	public List<DivisionResponse> updateBatchDivisionStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Division> divisions = this.findAllById(ids);
		List<AuditFields> auditFields = new ArrayList<>();
		divisions.forEach(existingDivision -> {
			if (existingDivision.getDivStatus() != null) {
				auditFields.add(new AuditFields(null, "Div Status", existingDivision.getDivStatus(),
						!existingDivision.getDivStatus()));
				existingDivision.setDivStatus(!existingDivision.getDivStatus());
			}
			existingDivision.updateAuditHistory(auditFields);
		});
		divisionRepository.saveAll(divisions);
		return divisions.stream().map(divisionMapper::mapToDivisionResponse).toList();
	}

	@Override
	public void deleteDivisionId(@NonNull Long id) throws ResourceNotFoundException {
		Division division = this.findDivisionById(id);
		if (division != null) {
			divisionRepository.delete(division);
		}
	}

	@Override
	public void deleteBatchDivision(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Division> divisions = this.findAllById(ids);
		if (!divisions.isEmpty()) {
			divisionRepository.deleteAll(divisions);
		}
	}

	private void validateDynamicFields(Division division) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : division.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = Division.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private Division findDivisionById(@NonNull Long id) throws ResourceNotFoundException {
		return divisionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Division found with this Id"));
	}

	private List<Division> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Division> divisions = divisionRepository.findAllById(ids);
		Set<Long> idSet = divisions.stream().map(Division::getId).collect(Collectors.toSet());
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();
		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Division with IDs " + missingIds + " not found.");
		}
		return divisions;
	}

}
