package com.example.generalservice.service;

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
	public IndustrySectorResponse updateSector(Long id, IndustrySectorRequest updateindustrysectorrequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String sectorCode = updateindustrysectorrequest.getSectorCode();
		String sectorName = updateindustrysectorrequest.getSectorName();
		IndustrySector existingIndustrySector = this.findSectorById(id);
		boolean exists = sectorRepository.existsBySectorCodeAndIdNotOrSectorNameAndIdNot(sectorCode, id, sectorName,
				id);
		if (!exists) {
			modelMapper.map(updateindustrysectorrequest, existingIndustrySector);
			for (Map.Entry<String, Object> entryField : existingIndustrySector.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = IndustrySector.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			IndustrySector updatedIndustrySector = sectorRepository.save(existingIndustrySector);
			return mapToIndustrySectorResponse(updatedIndustrySector);
		}
		throw new ResourceFoundException("Industry Sector Already Exist");
	}

	@Override
	public IndustrySectorResponse updateSectorStatus(Long id) throws ResourceNotFoundException {
		IndustrySector existingSector = this.findSectorById(id);
		existingSector.setSectorStatus(!existingSector.getSectorStatus());
		sectorRepository.save(existingSector);
		return this.mapToIndustrySectorResponse(existingSector);
	}

	@Override
	public List<IndustrySectorResponse> updateBatchSectorResponseStatus(List<Long> ids) {
		List<IndustrySector> industrySectors = sectorRepository.findAllById(ids);
		industrySectors.forEach(industrySector -> {
			industrySector.setSectorStatus(!industrySector.getSectorStatus());
			sectorRepository.save(industrySector);
		});
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
