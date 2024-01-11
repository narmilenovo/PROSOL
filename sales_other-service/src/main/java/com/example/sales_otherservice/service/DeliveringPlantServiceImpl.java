package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DpPlant;
import com.example.sales_otherservice.clients.DynamicClient;
import com.example.sales_otherservice.clients.PlantClient;
import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.entity.DeliveringPlant;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.DeliveringPlantRepository;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveringPlantServiceImpl implements DeliveringPlantService {
	private final DeliveringPlantRepository deliveringPlantRepository;
	private final PlantClient plantClient;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException {
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
	public List<DeliveringPlantResponse> getAllDp() {
		List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAll();
		return deliveringPlants.stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(this::mapToDeliveringPlantResponse).toList();
	}

	@Override
	public DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		return mapToDeliveringPlantResponse(deliveringPlant);
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
		String dpCode = updateDeliveringPlantRequest.getDpCode();
		String dpName = updateDeliveringPlantRequest.getDpName();
		DeliveringPlant existingDeliveringPlant = this.findDpById(id);
		boolean exist = deliveringPlantRepository.existsByDpCodeAndIdNotOrDpNameAndIdNot(dpCode, id, dpName, id);
		if (!exist) {
			modelMapper.map(updateDeliveringPlantRequest, existingDeliveringPlant);
			for (Map.Entry<String, Object> entryField : existingDeliveringPlant.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = DeliveringPlant.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			existingDeliveringPlant.setId(id);
			// existingDeliveringPlant.setId(existingDeliveringPlant.getId());
			DeliveringPlant updatedDeliveringPlant = deliveringPlantRepository.save(existingDeliveringPlant);
			return mapToDeliveringPlantResponse(updatedDeliveringPlant);
		}
		throw new ResourceFoundException("Delivering Plant already exist");
	}

	@Override
	public void deleteDpId(Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		deliveringPlantRepository.deleteById(deliveringPlant.getId());
	}

	@Override
	public void deleteBatchDp(List<Long> ids) {
		deliveringPlantRepository.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<DpPlant> getAllDpPlant() {
		return deliveringPlantRepository.findAll().stream().sorted(Comparator.comparing(DeliveringPlant::getId))
				.map(this::mapToDpPlant).toList();
	}

	@Override
	public DpPlant getDpPlantById(Long id) throws ResourceNotFoundException {
		DeliveringPlant deliveringPlant = this.findDpById(id);
		return mapToDpPlant(deliveringPlant);
	}

	private DeliveringPlantResponse mapToDeliveringPlantResponse(DeliveringPlant deliveringPlant) {
		return modelMapper.map(deliveringPlant, DeliveringPlantResponse.class);
	}

	private DpPlant mapToDpPlant(DeliveringPlant deliveringPlant) {
		DpPlant dpPlant = modelMapper.map(deliveringPlant, DpPlant.class);
		// Check if the id is null before getting the plant information
		if (deliveringPlant.getPlantId() != null) {
			dpPlant.setPlant(plantClient.getPlantById(deliveringPlant.getPlantId()));
		}
		return dpPlant;
	}

	private DeliveringPlant findDpById(Long id) throws ResourceNotFoundException {
		Optional<DeliveringPlant> deliveringPlant = deliveringPlantRepository.findById(id);
		if (deliveringPlant.isEmpty()) {
			throw new ResourceNotFoundException("Delivering Plant not found with this Id");
		}
		return deliveringPlant.get();
	}

}
