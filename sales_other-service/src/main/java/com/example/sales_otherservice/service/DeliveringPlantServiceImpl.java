package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DpPlant;
import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.clients.Plant.PlantResponse;
import com.example.sales_otherservice.clients.Plant.PlantServiceClient;
import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.DeliveringPlant;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.DeliveringPlantRepository;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveringPlantServiceImpl implements DeliveringPlantService {
	private final DeliveringPlantRepository deliveringPlantRepository;
	private final PlantServiceClient plantClient;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(deliveringPlantRequest);
		String dpCode = deliveringPlantRequest.getDpCode();
		String dpName = deliveringPlantRequest.getDpName();
		boolean exists = deliveringPlantRepository.existsByDpCodeOrDpName(dpCode, dpName);
		if (!exists) {
			DeliveringPlant deliveringPlant = modelMapper.map(deliveringPlantRequest, DeliveringPlant.class);
			for (Map.Entry<String, Object> entryField : deliveringPlant.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = DeliveringPlant.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			deliveringPlant.setId(null);
			DeliveringPlant savedPlant = deliveringPlantRepository.save(deliveringPlant);
			return mapToDeliveringPlantResponse(savedPlant);
		}
		throw new ResourceFoundException("Delivering Plant already exist");
	}

	@Override
	public DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		return mapToDeliveringPlantResponse(deliveringPlant);
	}

	@Override
	public DpPlant getDpPlantById(Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		return mapToDpPlant(deliveringPlant);
	}

	@Override
	public List<DeliveringPlantResponse> getAllDp() {
		List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAll();
		return deliveringPlants.stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(this::mapToDeliveringPlantResponse).toList();
	}

	@Override
	public List<DpPlant> getAllDpPlant() {
		return deliveringPlantRepository.findAll().stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(this::mapToDpPlant).toList();
	}

	@Override
	public List<DeliveringPlantResponse> findAllStatusTrue() {
		List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAllByDpStatusIsTrue();
		return deliveringPlants.stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(this::mapToDeliveringPlantResponse).toList();
	}

	@Override
	public DeliveringPlantResponse updateDp(Long id, DeliveringPlantRequest updateDeliveringPlantRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateDeliveringPlantRequest);
		String dpCode = updateDeliveringPlantRequest.getDpCode();
		String dpName = updateDeliveringPlantRequest.getDpName();
		DeliveringPlant existingDeliveringPlant = this.findDpById(id);
		boolean exist = deliveringPlantRepository.existsByDpCodeAndIdNotOrDpNameAndIdNot(dpCode, id, dpName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exist) {
			if (!existingDeliveringPlant.getDpCode().equals(dpCode)) {
				auditFields.add(new AuditFields(null, "Dp Code", existingDeliveringPlant.getDpCode(), dpCode));
				existingDeliveringPlant.setDpCode(dpCode);
			}
			if (!existingDeliveringPlant.getDpName().equals(dpName)) {
				auditFields.add(new AuditFields(null, "Dp Name", existingDeliveringPlant.getDpName(), dpName));
				existingDeliveringPlant.setDpName(dpName);
			}
			if (!existingDeliveringPlant.getDpStatus().equals(updateDeliveringPlantRequest.getDpStatus())) {
				auditFields.add(new AuditFields(null, "Dp Status", existingDeliveringPlant.getDpStatus(),
						updateDeliveringPlantRequest.getDpStatus()));
				existingDeliveringPlant.setDpStatus(updateDeliveringPlantRequest.getDpStatus());
			}
			if (!existingDeliveringPlant.getPlantId().equals(updateDeliveringPlantRequest.getPlantId())) {
				auditFields.add(new AuditFields(null, "Plant", existingDeliveringPlant.getPlantId(),
						updateDeliveringPlantRequest.getPlantId()));
				existingDeliveringPlant.setPlantId(updateDeliveringPlantRequest.getPlantId());
			}

			if (!existingDeliveringPlant.getDynamicFields().equals(updateDeliveringPlantRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateDeliveringPlantRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingDeliveringPlant.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingDeliveringPlant.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingDeliveringPlant.updateAuditHistory(auditFields);
			DeliveringPlant updatedDeliveringPlant = deliveringPlantRepository.save(existingDeliveringPlant);
			return mapToDeliveringPlantResponse(updatedDeliveringPlant);
		}
		throw new ResourceFoundException("Delivering Plant already exist");
	}

	@Override
	public DeliveringPlantResponse updateDpStatus(Long id) throws ResourceNotFoundException {
		DeliveringPlant existingDeliveringPlant = this.findDpById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingDeliveringPlant.getDpStatus() != null) {
			auditFields.add(new AuditFields(null, "Dp Status", existingDeliveringPlant.getDpStatus(),
					!existingDeliveringPlant.getDpStatus()));
			existingDeliveringPlant.setDpStatus(!existingDeliveringPlant.getDpStatus());
		}
		existingDeliveringPlant.updateAuditHistory(auditFields);
		deliveringPlantRepository.save(existingDeliveringPlant);
		return mapToDeliveringPlantResponse(existingDeliveringPlant);
	}

	@Override
	public List<DeliveringPlantResponse> updateBatchDpStatus(List<Long> ids) throws ResourceNotFoundException {
		List<DeliveringPlant> deliveringPlants = this.findAllDpById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		deliveringPlants.forEach(existingDeliveringPlant -> {
			if (existingDeliveringPlant.getDpStatus() != null) {
				auditFields.add(new AuditFields(null, "Dp Status", existingDeliveringPlant.getDpStatus(),
						!existingDeliveringPlant.getDpStatus()));
				existingDeliveringPlant.setDpStatus(!existingDeliveringPlant.getDpStatus());
			}
			existingDeliveringPlant.updateAuditHistory(auditFields);

		});
		deliveringPlantRepository.saveAll(deliveringPlants);
		return deliveringPlants.stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(this::mapToDeliveringPlantResponse).toList();
	}

	@Override
	public void deleteDpId(Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		deliveringPlantRepository.deleteById(deliveringPlant.getId());
	}

	@Override
	public void deleteBatchDp(List<Long> ids) throws ResourceNotFoundException {
		this.findAllDpById(ids);
		deliveringPlantRepository.deleteAllByIdInBatch(ids);
	}

	private DeliveringPlantResponse mapToDeliveringPlantResponse(DeliveringPlant deliveringPlant) {
		return modelMapper.map(deliveringPlant, DeliveringPlantResponse.class);
	}

	private DpPlant mapToDpPlant(DeliveringPlant deliveringPlant) {
		DpPlant dpPlant = modelMapper.map(deliveringPlant, DpPlant.class);
		// Check if the id is null before getting the plant information
		PlantResponse plant = plantClient.getPlantById(deliveringPlant.getPlantId());
		plant.setDynamicFields(plant.getDynamicFields());
		dpPlant.setPlant(plant);
		return dpPlant;
	}

	private DeliveringPlant findDpById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<DeliveringPlant> deliveringPlant = deliveringPlantRepository.findById(id);
		if (deliveringPlant.isEmpty()) {
			throw new ResourceNotFoundException("Delivering Plant not found with this Id");
		}
		return deliveringPlant.get();
	}

	private List<DeliveringPlant> findAllDpById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> deliveringPlants.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Delivering Plant with IDs " + missingIds + " not found.");
		}
		return deliveringPlants;
	}

}
