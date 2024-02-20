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
import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.entity.AlternateUOM;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.AlternateUOMRepository;
import com.example.generalservice.service.interfaces.AlternateUOMService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlternateUOMServiceImpl implements AlternateUOMService {
	private final AlternateUOMRepository alternateUOMRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(alternateUOMRequest);
		String uomCode = alternateUOMRequest.getUomCode();
		String uomName = alternateUOMRequest.getUomName();
		boolean exists = alternateUOMRepository.existsByUomCodeOrUomName(uomCode, uomName);
		if (!exists) {
			AlternateUOM alternateUOM = modelMapper.map(alternateUOMRequest, AlternateUOM.class);
			for (Map.Entry<String, Object> entryField : alternateUOM.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AlternateUOM.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			AlternateUOM savedUom = alternateUOMRepository.save(alternateUOM);
			return mapToAlternateUOMResponse(savedUom);

		}
		throw new ResourceFoundException("Uom Already Exist");
	}

	@Override
	@Cacheable("uom")
	public AlternateUOMResponse getUomById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		AlternateUOM uom = this.findUomById(id);
		return mapToAlternateUOMResponse(uom);
	}

	@Override
	@Cacheable("uom")
	public List<AlternateUOMResponse> getAllUom() throws ResourceNotFoundException {
		List<AlternateUOM> uomList = alternateUOMRepository.findAll();
		if (uomList.isEmpty()) {
			throw new ResourceNotFoundException("Attribute Uom is Empty");
		} else {
			return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId))
					.map(this::mapToAlternateUOMResponse).toList();
		}
	}

	@Override
	@Cacheable("uom")
	public List<AlternateUOMResponse> findAllStatusTrue() throws ResourceNotFoundException {
		List<AlternateUOM> uomList = alternateUOMRepository.findAllByUomStatusIsTrue();
		if (uomList.isEmpty()) {
			throw new ResourceNotFoundException("Attribute Uom is Empty");
		} else {

			return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId))
					.map(this::mapToAlternateUOMResponse).toList();
		}
	}

	@Override
	public AlternateUOMResponse updateUom(Long id, AlternateUOMRequest updateAlternateUOMRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateAlternateUOMRequest);
		String uomCode = updateAlternateUOMRequest.getUomCode();
		String uomName = updateAlternateUOMRequest.getUomName();
		AlternateUOM existingUom = this.findUomById(id);
		boolean exists = alternateUOMRepository.existsByUomCodeAndIdNotOrUomNameAndIdNot(uomCode, id, uomName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingUom.getUomCode().equals(uomCode)) {
				auditFields.add(new AuditFields(null, "Uom Code", existingUom.getUomCode(), uomCode));
				existingUom.setUomCode(uomCode);
			}
			if (!existingUom.getUomName().equals(uomName)) {
				auditFields.add(new AuditFields(null, "Uom Name", existingUom.getUomName(), uomName));
				existingUom.setUomName(uomName);
			}
			if (!existingUom.getUomStatus().equals(updateAlternateUOMRequest.getUomStatus())) {
				auditFields.add(new AuditFields(null, "Uom Status", existingUom.getUomStatus(),
						updateAlternateUOMRequest.getUomStatus()));
				existingUom.setUomStatus(updateAlternateUOMRequest.getUomStatus());
			}
			if (!existingUom.getDynamicFields().equals(updateAlternateUOMRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateAlternateUOMRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingUom.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingUom.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingUom.updateAuditHistory(auditFields); // Update the audit history
			AlternateUOM updatedUom = alternateUOMRepository.save(existingUom);
			return mapToAlternateUOMResponse(updatedUom);
		}
		throw new ResourceFoundException("Uom Already Exist");
	}

	@Override
	public AlternateUOMResponse updateUomStatus(Long id) throws ResourceNotFoundException {
		AlternateUOM existingUom = this.findUomById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingUom.getUomStatus() != null) {
			auditFields
					.add(new AuditFields(null, "Uom Status", existingUom.getUomStatus(), !existingUom.getUomStatus()));
			existingUom.setUomStatus(!existingUom.getUomStatus());
		}
		existingUom.updateAuditHistory(auditFields);
		alternateUOMRepository.save(existingUom);
		return mapToAlternateUOMResponse(existingUom);
	}

	@Override
	public List<AlternateUOMResponse> updateBatchUomStatus(List<Long> ids) throws ResourceNotFoundException {
		List<AlternateUOM> uomList = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		uomList.forEach(existingUom -> {
			if (existingUom.getUomStatus() != null) {
				auditFields.add(
						new AuditFields(null, "Uom Status", existingUom.getUomStatus(), !existingUom.getUomStatus()));
				existingUom.setUomStatus(!existingUom.getUomStatus());
			}
			existingUom.updateAuditHistory(auditFields);
		});
		alternateUOMRepository.saveAll(uomList);
		return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId)).map(this::mapToAlternateUOMResponse)
				.toList();
	}

	@Override
	public void deleteUomId(Long id) throws ResourceNotFoundException {
		AlternateUOM uom = this.findUomById(id);
		alternateUOMRepository.deleteById(uom.getId());
	}

	@Override
	public void deleteBatchUom(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		alternateUOMRepository.deleteAllByIdInBatch(ids);

	}

	private AlternateUOMResponse mapToAlternateUOMResponse(AlternateUOM alternateUOM) {
		return modelMapper.map(alternateUOM, AlternateUOMResponse.class);
	}

	private AlternateUOM findUomById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<AlternateUOM> uom = alternateUOMRepository.findById(id);
		if (uom.isEmpty()) {
			throw new ResourceNotFoundException("No Uom found with this");
		}
		return uom.get();
	}

	private List<AlternateUOM> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<AlternateUOM> uomList = alternateUOMRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> uomList.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return uomList;
	}

}
