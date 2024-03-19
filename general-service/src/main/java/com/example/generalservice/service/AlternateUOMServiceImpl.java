package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.entity.AlternateUOM;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.AlternateUOMMapper;
import com.example.generalservice.repository.AlternateUOMRepository;
import com.example.generalservice.service.interfaces.AlternateUOMService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlternateUOMServiceImpl implements AlternateUOMService {
	private final AlternateUOMRepository alternateUOMRepository;
	private final AlternateUOMMapper alternateUOMMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(alternateUOMRequest);
		String uomCode = alternateUOMRequest.getUomCode();
		String uomName = alternateUOMRequest.getUomName();
		if (alternateUOMRepository.existsByUomCodeOrUomName(uomCode, uomName)) {
			throw new ResourceFoundException("Uom Already Exist");
		}
		AlternateUOM alternateUOM = alternateUOMMapper.mapToAlternateUOM(alternateUOMRequest);
		validateDynamicFields(alternateUOM);
		AlternateUOM savedUom = alternateUOMRepository.save(alternateUOM);
		return alternateUOMMapper.mapToAlternateUOMResponse(savedUom);

	}

	@Override
	public AlternateUOMResponse getUomById(@NonNull Long id) throws ResourceNotFoundException {
		AlternateUOM uom = this.findUomById(id);
		return alternateUOMMapper.mapToAlternateUOMResponse(uom);
	}

	@Override
	public List<AlternateUOMResponse> getAllUom() throws ResourceNotFoundException {
		List<AlternateUOM> uomList = alternateUOMRepository.findAll();
		if (uomList.isEmpty()) {
			throw new ResourceNotFoundException("Attribute Uom is Empty");
		} else {
			return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId))
					.map(alternateUOMMapper::mapToAlternateUOMResponse).toList();
		}
	}

	@Override
	public List<AlternateUOMResponse> findAllStatusTrue() throws ResourceNotFoundException {
		List<AlternateUOM> uomList = alternateUOMRepository.findAllByUomStatusIsTrue();
		if (uomList.isEmpty()) {
			throw new ResourceNotFoundException("Attribute Uom is Empty");
		} else {

			return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId))
					.map(alternateUOMMapper::mapToAlternateUOMResponse).toList();
		}
	}

	@Override
	public AlternateUOMResponse updateUom(@NonNull Long id, AlternateUOMRequest updateAlternateUOMRequest)
			throws ResourceNotFoundException, ResourceFoundException {
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
			return alternateUOMMapper.mapToAlternateUOMResponse(updatedUom);
		}
		throw new ResourceFoundException("Uom Already Exist");
	}

	@Override
	public AlternateUOMResponse updateUomStatus(@NonNull Long id) throws ResourceNotFoundException {
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
		return alternateUOMMapper.mapToAlternateUOMResponse(existingUom);
	}

	@Override
	public List<AlternateUOMResponse> updateBatchUomStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<AlternateUOM> uomList = this.findAllById(ids);
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
		return uomList.stream().map(alternateUOMMapper::mapToAlternateUOMResponse).toList();
	}

	@Override
	public void deleteUomId(@NonNull Long id) throws ResourceNotFoundException {
		AlternateUOM uom = this.findUomById(id);
		if (uom != null) {
			alternateUOMRepository.delete(uom);
		}
	}

	@Override
	public void deleteBatchUom(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<AlternateUOM> alternateUOMs = this.findAllById(ids);
		if (!alternateUOMs.isEmpty()) {
			alternateUOMRepository.deleteAll(alternateUOMs);
		}

	}

	private void validateDynamicFields(AlternateUOM alternateUOM) throws ResourceNotFoundException {
		for (String fieldName : alternateUOM.getDynamicFields().keySet()) {
			if (!dynamicClient.checkFieldNameInForm(fieldName, AlternateUOM.class.getSimpleName())) {
				throw new ResourceNotFoundException(
						"Field '" + fieldName + "' does not exist in Dynamic Field creation.");
			}
		}
	}

	private AlternateUOM findUomById(@NonNull Long id) throws ResourceNotFoundException {
		return alternateUOMRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Uom found with this"));
	}

	private List<AlternateUOM> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<AlternateUOM> uomList = alternateUOMRepository.findAllById(ids);
		Set<Long> idSet = new HashSet<>(ids);
		List<AlternateUOM> missingUOMs = uomList.stream().filter(uom -> !idSet.contains(uom.getId())).toList();
		if (!missingUOMs.isEmpty()) {
			throw new ResourceNotFoundException(
					"Alternate Uom with IDs " + missingUOMs.stream().map(AlternateUOM::getId).toList() + " not found.");
		}
		return uomList;
	}

}
