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
import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.IndustrySector;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.IndustrySectorMapper;
import com.example.generalservice.repository.IndustrySectorRepository;
import com.example.generalservice.service.interfaces.IndustrySectorService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndustrySectorServiceImpl implements IndustrySectorService {
	private final IndustrySectorRepository sectorRepository;
	private final IndustrySectorMapper industrySectorMapper;
	private final DynamicClient dynamicClient;

	@Override
	public IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(industrySectorRequest);
		String sectorCode = industrySectorRequest.getSectorCode();
		String sectorName = industrySectorRequest.getSectorName();
		boolean exists = sectorRepository.existsBySectorCodeOrSectorName(sectorCode, sectorName);
		if (!exists) {
			IndustrySector industrySector = industrySectorMapper.mapToIndustrySector(industrySectorRequest);
			validateDynamicFields(industrySector);

			IndustrySector savedSector = sectorRepository.save(industrySector);
			return industrySectorMapper.mapToIndustrySectorResponse(savedSector);
		}
		throw new ResourceFoundException("Industry Sector Already Exist");
	}

	@Override
	public IndustrySectorResponse getSectorById(@NonNull Long id) throws ResourceNotFoundException {
		IndustrySector industrySector = this.findSectorById(id);
		return industrySectorMapper.mapToIndustrySectorResponse(industrySector);

	}

	@Override
	public List<IndustrySectorResponse> getAllSector() {
		return sectorRepository.findAll().stream().sorted(Comparator.comparing(IndustrySector::getId))
				.map(industrySectorMapper::mapToIndustrySectorResponse).toList();
	}

	@Override
	public List<IndustrySectorResponse> findAllStatusTrue() {
		return sectorRepository.findAllBySectorStatusIsTrue().stream()
				.sorted(Comparator.comparing(IndustrySector::getId))
				.map(industrySectorMapper::mapToIndustrySectorResponse).toList();
	}

	@Override
	public IndustrySectorResponse updateSector(@NonNull Long id, IndustrySectorRequest updateIndustrySectorRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateIndustrySectorRequest);
		String sectorCode = updateIndustrySectorRequest.getSectorCode();
		String sectorName = updateIndustrySectorRequest.getSectorName();
		IndustrySector existingIndustrySector = this.findSectorById(id);
		boolean exists = sectorRepository.existsBySectorCodeAndIdNotOrSectorNameAndIdNot(sectorCode, id, sectorName,
				id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingIndustrySector.getSectorCode().equals(sectorCode)) {
				auditFields
						.add(new AuditFields(null, "Sector Code", existingIndustrySector.getSectorCode(), sectorCode));
				existingIndustrySector.setSectorCode(sectorCode);
			}
			if (!existingIndustrySector.getSectorName().equals(sectorName)) {
				auditFields
						.add(new AuditFields(null, "Sector Name", existingIndustrySector.getSectorName(), sectorName));
				existingIndustrySector.setSectorName(sectorName);
			}
			if (!existingIndustrySector.getSectorStatus().equals(updateIndustrySectorRequest.getSectorStatus())) {
				auditFields.add(new AuditFields(null, "Sector Status", existingIndustrySector.getSectorStatus(),
						updateIndustrySectorRequest.getSectorStatus()));
				existingIndustrySector.setSectorStatus(updateIndustrySectorRequest.getSectorStatus());
			}
			if (!existingIndustrySector.getDynamicFields().equals(updateIndustrySectorRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateIndustrySectorRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingIndustrySector.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingIndustrySector.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingIndustrySector.updateAuditHistory(auditFields);
			IndustrySector updatedIndustrySector = sectorRepository.save(existingIndustrySector);
			return industrySectorMapper.mapToIndustrySectorResponse(updatedIndustrySector);
		}
		throw new ResourceFoundException("Industry Sector Already Exist");
	}

	@Override
	public IndustrySectorResponse updateSectorStatus(@NonNull Long id) throws ResourceNotFoundException {
		IndustrySector existingSector = this.findSectorById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingSector.getSectorStatus() != null) {
			auditFields.add(new AuditFields(null, "Sector Status", existingSector.getSectorStatus(),
					!existingSector.getSectorStatus()));
			existingSector.setSectorStatus(!existingSector.getSectorStatus());
		}
		existingSector.updateAuditHistory(auditFields);
		sectorRepository.save(existingSector);
		return industrySectorMapper.mapToIndustrySectorResponse(existingSector);
	}

	@Override
	public List<IndustrySectorResponse> updateBatchSectorResponseStatus(@NonNull List<Long> ids) {
		List<IndustrySector> industrySectors = sectorRepository.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		industrySectors.forEach(existingSector -> {
			if (existingSector.getSectorStatus() != null) {
				auditFields.add(new AuditFields(null, "Sector Status", existingSector.getSectorStatus(),
						!existingSector.getSectorStatus()));
				existingSector.setSectorStatus(!existingSector.getSectorStatus());
			}
			existingSector.updateAuditHistory(auditFields);

		});
		sectorRepository.saveAll(industrySectors);
		return industrySectors.stream().sorted(Comparator.comparing(IndustrySector::getId))
				.map(industrySectorMapper::mapToIndustrySectorResponse).toList();
	}

	@Override
	public void deleteSectorId(@NonNull Long id) throws ResourceNotFoundException {
		IndustrySector industrySector = this.findSectorById(id);
		if (industrySector != null) {
			sectorRepository.delete(industrySector);
		}
	}

	@Override
	public void deleteBatchSector(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<IndustrySector> industrySectors = this.findAllById(ids);
		if (!industrySectors.isEmpty()) {
			sectorRepository.deleteAll(industrySectors);
		}
	}

	private void validateDynamicFields(IndustrySector industrySector) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : industrySector.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = IndustrySector.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private IndustrySector findSectorById(@NonNull Long id) throws ResourceNotFoundException {
		return sectorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Industry Sector found with this Id"));
	}

	private List<IndustrySector> findAllById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<IndustrySector> industrySectors = sectorRepository.findAllById(ids);
		Set<Long> idSet = industrySectors.stream().map(IndustrySector::getId).collect(Collectors.toSet());
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).collect(Collectors.toList());
		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Industry Sector with IDs " + missingIds + " not found.");
		}
		return industrySectors;
	}

}
