package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.PlantMapper;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {
	private final PlantRepo plantRepo;
	private final PlantMapper plantMapper;
	private final ExcelFileHelper excelFileHelper;
	private final DynamicClient dynamicClient;

	@Override
	public PlantResponse savePlant(PlantRequest plantRequest)
			throws AlreadyExistsException, ResourceNotFoundException, IllegalAccessException {
		Helpers.inputTitleCase(plantRequest);
		String plantCode = plantRequest.getPlantCode();
		String plantName = plantRequest.getPlantName();
		if (plantRepo.existsByPlantCodeAndPlantName(plantCode, plantName)) {
			throw new AlreadyExistsException("Plant with this name already exists");
		}
		Plant plant = plantMapper.mapToPlant(plantRequest);
		validateDynamicFields(plant);
		plantRepo.save(plant);
		return plantMapper.mapToPlantResponse(plant);
	}

	@Override
	public List<PlantResponse> saveAllPlant(@Valid List<PlantRequest> plantRequests)
			throws AlreadyExistsException, ResourceNotFoundException {
		List<Plant> plants = new ArrayList<>();
		for (PlantRequest plantRequest : plantRequests) {
			Helpers.inputTitleCase(plantRequest);
			String plantCode = plantRequest.getPlantCode();
			String plantName = plantRequest.getPlantName();
			if (plantRepo.existsByPlantCodeAndPlantName(plantCode, plantName)) {
				throw new AlreadyExistsException("Plant with this name already exists");
			}
			Plant plant = plantMapper.mapToPlant(plantRequest);
			validateDynamicFields(plant);
			plants.add(plant);
		}
		plantRepo.saveAll(plants);
		return plantMapper.mapToPlantResponseList(plants);
	}

	@Override
	public PlantResponse getPlantById(Long plantId) throws ResourceNotFoundException {
		Plant plant = this.findPlantById(plantId);
		return plantMapper.mapToPlantResponse(plant);
	}

	@Override
	public PlantResponse getPlantByName(String name) throws ResourceNotFoundException {
		Plant plant = this.findPlantByName(name);
		return plantMapper.mapToPlantResponse(plant);
	}

	@Override
	public List<PlantResponse> getAllPlants() {
		return plantRepo.findAllByOrderByIdAsc().stream().map(plantMapper::mapToPlantResponse).toList();
	}

	@Override
	public List<Plant> findAll() {
		return plantRepo.findAllByOrderByIdAsc();
	}

	@Override
	public PlantResponse updatePlant(Long id, PlantRequest plantRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(plantRequest);
		String plantName = plantRequest.getPlantName();
		String plantCode = plantRequest.getPlantCode();
		boolean exists = plantRepo.existsByPlantCodeAndPlantNameAndIdNot(plantCode, plantName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			Plant existingPlant = this.findPlantById(id);
			if (!existingPlant.getPlantCode().equals(plantCode)) {
				auditFields.add(new AuditFields(null, "Plant Code", existingPlant.getPlantCode(), plantCode));
				existingPlant.setPlantCode(plantCode);
			}
			if (!existingPlant.getPlantName().equals(plantName)) {
				auditFields.add(new AuditFields(null, "Plant Name", existingPlant.getPlantName(), plantName));
				existingPlant.setPlantName(plantName);
			}
			if (!existingPlant.getPlantStatus().equals(plantRequest.getPlantStatus())) {
				auditFields.add(new AuditFields(null, "Plant Status", existingPlant.getPlantStatus(),
						plantRequest.getPlantStatus()));
				existingPlant.setPlantStatus(plantRequest.getPlantStatus());
			}
			if (!existingPlant.getDynamicFields().equals(plantRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : plantRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingPlant.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingPlant.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingPlant.updateAuditHistory(auditFields);
			plantRepo.save(existingPlant);
			return plantMapper.mapToPlantResponse(existingPlant);
		} else {
			throw new AlreadyExistsException("Plant with this name already exists");
		}
	}

	@Override
	public List<PlantResponse> updateBulkStatusPlantId(List<Long> id) throws ResourceNotFoundException {
		List<Plant> existingPlants = this.findAllPlantById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingPlants.forEach(existingPlant -> {
			if (existingPlant.getPlantStatus() != null) {
				auditFields.add(new AuditFields(null, "Plant Status", existingPlant.getPlantStatus(),
						!existingPlant.getPlantStatus()));
				existingPlant.setPlantStatus(!existingPlant.getPlantStatus());
			}
			existingPlant.updateAuditHistory(auditFields);
		});
		plantRepo.saveAll(existingPlants);
		return existingPlants.stream().map(plantMapper::mapToPlantResponse).toList();
	}

	@Override
	public PlantResponse updateStatusUsingPlantId(Long id) throws ResourceNotFoundException {
		Plant existingPlant = this.findPlantById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingPlant.getPlantStatus() != null) {
			auditFields.add(new AuditFields(null, "Plant Status", existingPlant.getPlantStatus(),
					!existingPlant.getPlantStatus()));
			existingPlant.setPlantStatus(!existingPlant.getPlantStatus());
		}
		existingPlant.updateAuditHistory(auditFields);
		plantRepo.save(existingPlant);
		return plantMapper.mapToPlantResponse(existingPlant);
	}

	@Override
	public void deletePlant(Long id) throws ResourceNotFoundException {
		Plant plant = this.findPlantById(id);
		if (plant != null) {
			plantRepo.delete(plant);
		}
	}

	@Override
	public void deleteBatchPlant(List<Long> ids) throws ResourceNotFoundException {
		List<Plant> plants = this.findAllPlantById(ids);
		if (!plants.isEmpty()) {
			plantRepo.deleteAll(plants);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "Plant";
		Class<?> clazz = PlantRequest.class;
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Plant_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contentType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<Plant> plant = excelFileHelper.readDataFromExcel(file.getInputStream(), Plant.class);
		for (Plant data : plant) {
			if (!plantRepo.existsByPlantCodeAndPlantName(data.getPlantCode(), data.getPlantName())) {

				this.plantRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "Plant";
		Class<?> clazz = PlantResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Plant_";
		List<PlantResponse> allValue = getAllPlants();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertPlantListToMap(List<Plant> hsnList) {
		List<Map<String, Object>> data = new ArrayList<>();

		for (Plant plant : hsnList) {
			Map<String, Object> plantData = new HashMap<>();
			plantData.put("Id", plant.getId());
			plantData.put("Plant Code", plant.getPlantCode());
			plantData.put("Plant Desc", plant.getPlantName());
			plantData.put("Plant Status", plant.getPlantStatus());
			data.add(plantData);
		}
		return data;
	}

	private void validateDynamicFields(Plant plant) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : plant.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = Plant.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private Plant findPlantById(Long plantId) throws ResourceNotFoundException {
		return plantRepo.findById(plantId)
				.orElseThrow(() -> new ResourceNotFoundException("Plant with ID " + plantId + " not found"));
	}

	private Plant findPlantByName(String name) throws ResourceNotFoundException {
		return plantRepo.findByPlantName(name)
				.orElseThrow(() -> new ResourceNotFoundException("Plant with Name " + name + " not found"));
	}

	private List<Plant> findAllPlantById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<Plant> plants = plantRepo.findAllById(ids);
		List<Plant> foundPlants = plants.stream().filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Plants with IDs " + missingIds + " not found.");
		}
		return foundPlants;
	}

}
