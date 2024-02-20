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
import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.Division;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.DivisionRepository;
import com.example.generalservice.service.interfaces.DivisionService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {
	private final DivisionRepository divisionRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DivisionResponse saveDivision(DivisionRequest divisionRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(divisionRequest);
		String divCode = divisionRequest.getDivCode();
		String divName = divisionRequest.getDivName();
		boolean exists = divisionRepository.existsByDivCodeOrDivName(divCode, divName);
		if (!exists) {

			Division division = modelMapper.map(divisionRequest, Division.class);
			for (Map.Entry<String, Object> entryField : division.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Division.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			Division savedDivision = divisionRepository.save(division);
			return mapToDivisionResponse(savedDivision);
		}
		throw new ResourceFoundException("Division Already Exist");
	}

	@Override
	@Cacheable("div")
	public DivisionResponse getDivisionById(Long id) throws ResourceNotFoundException {
		Division division = this.findDivisionById(id);
		return mapToDivisionResponse(division);
	}

	@Override
	@Cacheable("div")
	public List<DivisionResponse> getAllDivision() {
		List<Division> divisionList = divisionRepository.findAll();
		return divisionList.stream().sorted(Comparator.comparing(Division::getId)).map(this::mapToDivisionResponse)
				.toList();
	}

	@Override
	@Cacheable("div")
	public List<DivisionResponse> findAllStatusTrue() {
		List<Division> divisionList = divisionRepository.findAllByDivStatusIsTrue();
		return divisionList.stream().sorted(Comparator.comparing(Division::getId)).map(this::mapToDivisionResponse)
				.toList();
	}

	@Override
	public DivisionResponse updateDivision(Long id, DivisionRequest updateDivisionRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateDivisionRequest);
		String divCode = updateDivisionRequest.getDivCode();
		String divName = updateDivisionRequest.getDivName();
		Division existingDivision = this.findDivisionById(id);
		boolean exists = divisionRepository.existsByDivCodeAndIdNotOrDivNameAndIdNot(divCode, id, divName, id);
		// Find properties that have changed
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
			return mapToDivisionResponse(updatedDivision);
		}
		throw new ResourceFoundException("Division Already Exist");
	}

	@Override
	public DivisionResponse updateDivisionStatus(Long id) throws ResourceNotFoundException {
		Division existingDivision = this.findDivisionById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingDivision.getDivStatus() != null) {
			auditFields.add(new AuditFields(null, "Div Status", existingDivision.getDivStatus(),
					!existingDivision.getDivStatus()));
			existingDivision.setDivStatus(!existingDivision.getDivStatus());
		}
		existingDivision.updateAuditHistory(auditFields);
		divisionRepository.save(existingDivision);
		return this.mapToDivisionResponse(existingDivision);
	}

	@Override
	public List<DivisionResponse> updateBatchDivisionStatus(List<Long> ids) throws ResourceNotFoundException {
		List<Division> divisions = this.findAllById(ids);
		// Find properties that have changed
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
		return divisions.stream().map(this::mapToDivisionResponse).toList();
	}

	@Override
	public void deleteDivisionId(Long id) throws ResourceNotFoundException {
		Division division = this.findDivisionById(id);
		divisionRepository.deleteById(division.getId());
	}

	@Override
	public void deleteBatchDivision(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		divisionRepository.deleteAllByIdInBatch(ids);
	}

	private DivisionResponse mapToDivisionResponse(Division division) {
		return modelMapper.map(division, DivisionResponse.class);
	}

	private Division findDivisionById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<Division> division = divisionRepository.findById(id);
		if (division.isEmpty()) {
			throw new ResourceNotFoundException("No Division found with this Id");
		}
		return division.get();
	}

	private List<Division> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<Division> divisions = divisionRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> divisions.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Division with IDs " + missingIds + " not found.");
		}
		return divisions;
	}

}
