package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
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
import com.example.sales_otherservice.mapping.DeliveringPlantMapper;
import com.example.sales_otherservice.repository.DeliveringPlantRepository;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveringPlantServiceImpl implements DeliveringPlantService {
	private final DeliveringPlantRepository deliveringPlantRepository;
	private final PlantServiceClient plantClient;
	private final DeliveringPlantMapper deliveringPlantMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(deliveringPlantRequest);
		String dpCode = deliveringPlantRequest.getDpCode();
		String dpName = deliveringPlantRequest.getDpName();
		if (deliveringPlantRepository.existsByDpCodeOrDpName(dpCode, dpName)) {
			throw new ResourceFoundException("Delivering Plant already exist");
		}
		DeliveringPlant deliveringPlant = deliveringPlantMapper.mapToDeliveringPlant(deliveringPlantRequest);

		validateDynamicFields(deliveringPlant);

		deliveringPlant.setId(null);
		DeliveringPlant savedPlant = deliveringPlantRepository.save(deliveringPlant);
		return deliveringPlantMapper.mapToDeliveringPlantResponse(savedPlant);
	}

	@Override
	public DeliveringPlantResponse getDpById(@NonNull Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		return deliveringPlantMapper.mapToDeliveringPlantResponse(deliveringPlant);
	}

	@Override
	public DpPlant getDpPlantById(@NonNull Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		return mapToDpPlant(deliveringPlant);
	}

	@Override
	public List<DeliveringPlantResponse> getAllDp() {
		List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAll();
		return deliveringPlants.stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(deliveringPlantMapper::mapToDeliveringPlantResponse).toList();
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
				.map(deliveringPlantMapper::mapToDeliveringPlantResponse).toList();
	}

	@Override
	public DeliveringPlantResponse updateDp(@NonNull Long id, DeliveringPlantRequest updateDeliveringPlantRequest)
			throws ResourceNotFoundException, ResourceFoundException {
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
			return deliveringPlantMapper.mapToDeliveringPlantResponse(updatedDeliveringPlant);
		}
		throw new ResourceFoundException("Delivering Plant already exist");
	}

	@Override
	public DeliveringPlantResponse updateDpStatus(@NonNull Long id) throws ResourceNotFoundException {
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
		return deliveringPlantMapper.mapToDeliveringPlantResponse(existingDeliveringPlant);
	}

	@Override
	public List<DeliveringPlantResponse> updateBatchDpStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
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
				.map(deliveringPlantMapper::mapToDeliveringPlantResponse).toList();
	}

	@Override
	public void deleteDpId(@NonNull Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		if (deliveringPlant != null) {
			deliveringPlantRepository.delete(deliveringPlant);
		}
	}

	@Override
	public void deleteBatchDp(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<DeliveringPlant> deliveringPlants = this.findAllDpById(ids);
		if (!deliveringPlants.isEmpty()) {
			deliveringPlantRepository.deleteAll(deliveringPlants);
		}
	}

	private void validateDynamicFields(DeliveringPlant deliveringPlant) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : deliveringPlant.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = DeliveringPlant.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private DpPlant mapToDpPlant(DeliveringPlant deliveringPlant) {
		DpPlant dpPlant = deliveringPlantMapper.mapToDpPlant(deliveringPlant);
		PlantResponse plant = plantClient.getPlantById(deliveringPlant.getPlantId());
		plant.setDynamicFields(plant.getDynamicFields());
		dpPlant.setPlant(plant);
		return dpPlant;
	}

	private DeliveringPlant findDpById(@NonNull Long id) throws ResourceNotFoundException {
		return deliveringPlantRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Delivering Plant not found with this Id"));
	}

	private List<DeliveringPlant> findAllDpById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAllById(ids);

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Delivering Plant with IDs " + missingIds + " not found.");
		}

		return deliveringPlants;
	}

}
