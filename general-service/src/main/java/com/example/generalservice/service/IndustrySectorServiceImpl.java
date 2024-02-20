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
import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.IndustrySector;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.IndustrySectorRepository;
import com.example.generalservice.service.interfaces.IndustrySectorService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndustrySectorServiceImpl implements IndustrySectorService {
	private final IndustrySectorRepository sectorRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(industrySectorRequest);
		String sectorCode = industrySectorRequest.getSectorCode();
		String sectorName = industrySectorRequest.getSectorName();
		boolean exists = sectorRepository.existsBySectorCodeOrSectorName(sectorCode, sectorName);
		if (!exists) {
			IndustrySector industrySector = modelMapper.map(industrySectorRequest, IndustrySector.class);
			for (Map.Entry<String, Object> entryField : industrySector.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = IndustrySector.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			IndustrySector savedSector = sectorRepository.save(industrySector);
			return mapToIndustrySectorResponse(savedSector);
		}
		throw new ResourceFoundException("Industry Sector Already Exist");
	}

	@Override
	@Cacheable("sector")
	public IndustrySectorResponse getSectorById(Long id) throws ResourceNotFoundException {
		IndustrySector industrySector = this.findSectorById(id);
		return mapToIndustrySectorResponse(industrySector);

	}

	@Override
	@Cacheable("sector")
	public List<IndustrySectorResponse> getAllSector() {
		List<IndustrySector> sectorResponses = sectorRepository.findAll();
		return sectorResponses.stream().sorted(Comparator.comparing(IndustrySector::getId))
				.map(this::mapToIndustrySectorResponse).toList();
	}

	@Override
	@Cacheable("sector")
	public List<IndustrySectorResponse> findAllStatusTrue() {
		List<IndustrySector> sectorResponses = sectorRepository.findAllBySectorStatusIsTrue();
		return sectorResponses.stream().sorted(Comparator.comparing(IndustrySector::getId))
				.map(this::mapToIndustrySectorResponse).toList();
	}

	@Override
	public IndustrySectorResponse updateSector(Long id, IndustrySectorRequest updateIndustrySectorRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
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
			return mapToIndustrySectorResponse(updatedIndustrySector);
		}
		throw new ResourceFoundException("Industry Sector Already Exist");
	}

	@Override
	public IndustrySectorResponse updateSectorStatus(Long id) throws ResourceNotFoundException {
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
		return this.mapToIndustrySectorResponse(existingSector);
	}

	@Override
	public List<IndustrySectorResponse> updateBatchSectorResponseStatus(List<Long> ids) {
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
				.map(this::mapToIndustrySectorResponse).toList();
	}

	@Override
	public void deleteSectorId(Long id) throws ResourceNotFoundException {
		IndustrySector industrySector = this.findSectorById(id);
		sectorRepository.deleteById(industrySector.getId());
	}

	@Override
	public void deleteBatchSector(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		sectorRepository.deleteAllByIdInBatch(ids);
	}

	private IndustrySectorResponse mapToIndustrySectorResponse(IndustrySector industrySector) {
		return modelMapper.map(industrySector, IndustrySectorResponse.class);
	}

	private IndustrySector findSectorById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<IndustrySector> sector = sectorRepository.findById(id);
		if (sector.isEmpty()) {
			throw new ResourceNotFoundException("No Industry Sector found with this Id");
		}
		return sector.get();
	}

	private List<IndustrySector> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<IndustrySector> industrySectors = sectorRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> industrySectors.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return industrySectors;
	}

}
