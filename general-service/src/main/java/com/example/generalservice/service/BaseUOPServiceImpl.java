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
import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.BaseUOP;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.BaseUOPMapper;
import com.example.generalservice.repository.BaseUOPRepository;
import com.example.generalservice.service.interfaces.BaseUOPService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BaseUOPServiceImpl implements BaseUOPService {
	private final BaseUOPRepository baseUOPRepository;
	private final BaseUOPMapper baseUOPMapper;
	private final DynamicClient dynamicClient;

	@Override
	public BaseUOPResponse saveUop(BaseUOPRequest baseUOPRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(baseUOPRequest);
		String uopCode = baseUOPRequest.getUopCode();
		String uopName = baseUOPRequest.getUopName();
		if (baseUOPRepository.existsByUopCodeOrUopName(uopCode, uopName)) {
			throw new ResourceFoundException("Uop Already Exists");
		}
		BaseUOP baseUOP = baseUOPMapper.mapToBaseUOP(baseUOPRequest);
		validateDynamicFields(baseUOP);
		BaseUOP savedUop = baseUOPRepository.save(baseUOP);
		return baseUOPMapper.mapToBaseUOPResponse(savedUop);
	}

	@Override
	public BaseUOPResponse getUopById(@NonNull Long id) throws ResourceNotFoundException {
		BaseUOP baseUOP = this.findUopById(id);
		return baseUOPMapper.mapToBaseUOPResponse(baseUOP);
	}

	@Override
	public List<BaseUOPResponse> getAllUop() {
		return baseUOPRepository.findAll().stream().sorted(Comparator.comparing(BaseUOP::getId))
				.map(baseUOPMapper::mapToBaseUOPResponse).toList();
	}

	@Override
	public List<BaseUOPResponse> findAllStatusTrue() {
		return baseUOPRepository.findAllByUopStatusIsTrue().stream().sorted(Comparator.comparing(BaseUOP::getId))
				.map(baseUOPMapper::mapToBaseUOPResponse).toList();
	}

	@Override
	public BaseUOPResponse updateUop(@NonNull Long id, BaseUOPRequest updateBaseUOPRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateBaseUOPRequest);
		String uopCode = updateBaseUOPRequest.getUopCode();
		String uopName = updateBaseUOPRequest.getUopName();
		BaseUOP existingBaseUOP = this.findUopById(id);
		boolean exists = baseUOPRepository.existsByUopCodeAndIdNotOrUopNameAndIdNot(uopCode, id, uopName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingBaseUOP.getUopCode().equals(uopCode)) {
				auditFields.add(new AuditFields(null, "Uop Code", existingBaseUOP.getUopCode(), uopCode));
				existingBaseUOP.setUopCode(uopCode);
			}
			if (!existingBaseUOP.getUopName().equals(uopName)) {
				auditFields.add(new AuditFields(null, "Uop Name", existingBaseUOP.getUopName(), uopName));
				existingBaseUOP.setUopName(uopName);
			}
			if (!existingBaseUOP.getUopStatus().equals(updateBaseUOPRequest.getUopStatus())) {
				auditFields.add(new AuditFields(null, "Uop Status", existingBaseUOP.getUopStatus(),
						updateBaseUOPRequest.getUopStatus()));
				existingBaseUOP.setUopStatus(updateBaseUOPRequest.getUopStatus());
			}
			if (!existingBaseUOP.getDynamicFields().equals(updateBaseUOPRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateBaseUOPRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingBaseUOP.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingBaseUOP.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingBaseUOP.updateAuditHistory(auditFields); // Update the audit history
			BaseUOP updatedBaseUOP = baseUOPRepository.save(existingBaseUOP);
			return baseUOPMapper.mapToBaseUOPResponse(updatedBaseUOP);
		}
		throw new ResourceFoundException("Uop Already Exist");
	}

	@Override
	public BaseUOPResponse updateUopStatus(@NonNull Long id) throws ResourceNotFoundException {
		BaseUOP existingBaseUOP = this.findUopById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingBaseUOP.getUopStatus() != null) {
			auditFields.add(new AuditFields(null, "Uop Status", existingBaseUOP.getUopStatus(),
					!existingBaseUOP.getUopStatus()));
			existingBaseUOP.setUopStatus(!existingBaseUOP.getUopStatus());
		}
		existingBaseUOP.updateAuditHistory(auditFields);
		baseUOPRepository.save(existingBaseUOP);
		return baseUOPMapper.mapToBaseUOPResponse(existingBaseUOP);
	}

	@Override
	public List<BaseUOPResponse> updateBatchUopStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<BaseUOP> baseUOPs = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		baseUOPs.forEach(existingBaseUOP -> {
			if (existingBaseUOP.getUopStatus() != null) {
				auditFields.add(new AuditFields(null, "Uop Status", existingBaseUOP.getUopStatus(),
						!existingBaseUOP.getUopStatus()));
				existingBaseUOP.setUopStatus(!existingBaseUOP.getUopStatus());
			}
			existingBaseUOP.updateAuditHistory(auditFields);
		});
		baseUOPRepository.saveAll(baseUOPs);
		return baseUOPs.stream().map(baseUOPMapper::mapToBaseUOPResponse).toList();
	}

	@Override
	public void deleteUopId(@NonNull Long id) throws ResourceNotFoundException {
		BaseUOP baseUOP = this.findUopById(id);
		if (baseUOP != null) {
			baseUOPRepository.delete(baseUOP);
		}
	}

	@Override
	public void deleteBatchUop(@NonNull List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		baseUOPRepository.deleteAllByIdInBatch(ids);
	}

	private void validateDynamicFields(BaseUOP baseUOP) throws ResourceNotFoundException {
		for (String fieldName : baseUOP.getDynamicFields().keySet()) {
			if (!dynamicClient.checkFieldNameInForm(fieldName, BaseUOP.class.getSimpleName())) {
				throw new ResourceNotFoundException(
						"Field of '" + fieldName + "' does not exist in Dynamic Field creation for form '"
								+ BaseUOP.class.getSimpleName() + "' !!");
			}
		}
	}

	private BaseUOP findUopById(@NonNull Long id) throws ResourceNotFoundException {
		return baseUOPRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Uop found with this Id"));
	}

	private List<BaseUOP> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<BaseUOP> baseUOPs = baseUOPRepository.findAllById(ids);
		Set<Long> idSet = baseUOPs.stream().map(BaseUOP::getId).collect(Collectors.toSet());
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();
		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Base UOP with IDs " + missingIds + " not found.");
		}
		return baseUOPs;
	}

}
