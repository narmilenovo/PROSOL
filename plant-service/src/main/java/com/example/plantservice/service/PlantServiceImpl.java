package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {
	private static final String PLANT_NOT_FOUND_MESSAGE = null;
	private final PlantRepo plantRepo;
	private final ModelMapper modelMapper;
	private final ExcelFileHelper excelFileHelper;
	private final DynamicClient dynamicClient;

	@Override
	public PlantResponse savePlant(PlantRequest plantRequest)
			throws AlreadyExistsException, ResourceNotFoundException, IllegalAccessException {
		boolean exists = plantRepo.existsByPlantCodeAndPlantName(plantRequest.getPlantCode(),
				plantRequest.getPlantName());
		if (!exists) {
			Plant plant = modelMapper.map(plantRequest, Plant.class);
			for (Map.Entry<String, Object> entryField : plant.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Plant.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			Helpers.capitalizeFields(plant);
			plantRepo.save(plant);
			return mapToPlantResponse(plant);
		} else {
			throw new AlreadyExistsException("Plant with this name already exists");
		}
	}

	@Override
	public PlantResponse getPlantById(Long plantId) throws ResourceNotFoundException {
		Plant plant = this.findPlantById(plantId);
		return mapToPlantResponse(plant);
	}

	@Override
	public PlantResponse getPlantByName(String name) throws ResourceNotFoundException {
		Plant plant = this.findPlantByName(name);
		return mapToPlantResponse(plant);
	}

	@Override
	public List<PlantResponse> getAllPlants() {
		List<Plant> plant = plantRepo.findAllByOrderByIdAsc();
		return plant.stream().map(this::mapToPlantResponse).toList();
	}

	@Override
	public List<Plant> findAll() {
		return plantRepo.findAllByOrderByIdAsc();
	}

	@Override
	public PlantResponse updatePlant(Long id, PlantRequest plantRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String plantName = plantRequest.getPlantName();
		String code = plantRequest.getPlantCode();
		boolean exists = plantRepo.existsByPlantCodeAndPlantNameAndIdNot(code, plantName, id);
		if (!exists) {
			Plant existingPlant = this.findPlantById(id);
			modelMapper.map(plantRequest, existingPlant);
			for (Map.Entry<String, Object> entryField : existingPlant.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Plant.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of: '" + fieldName
							+ "' not exist in Dynamic Field creation of form: '" + formName + "' !!!");
				}
			}
			plantRepo.save(existingPlant);
			return mapToPlantResponse(existingPlant);
		} else {
			throw new AlreadyExistsException("Plant with this name already exists");
		}
	}

	@Override
	public List<PlantResponse> updateBulkStatusPlantId(List<Long> id) throws ResourceNotFoundException {
		List<Plant> existingPlant = this.findAllPlantById(id);
		for (Plant plant : existingPlant) {
			plant.setPlantStatus(!plant.getPlantStatus());
		}
		plantRepo.saveAll(existingPlant);
		return existingPlant.stream().map(this::mapToPlantResponse).toList();
	}

	@Override
	public PlantResponse updateStatusUsingPlantId(Long id) throws ResourceNotFoundException {
		Plant existingPlant = this.findPlantById(id);
		existingPlant.setPlantStatus(!existingPlant.getPlantStatus());
		plantRepo.save(existingPlant);
		return mapToPlantResponse(existingPlant);
	}

	@Override
	public void deletePlant(Long id) throws ResourceNotFoundException {
		Plant plant = this.findPlantById(id);
		plantRepo.deleteById(plant.getId());
	}

	@Override
	public void deleteBatchPlant(List<Long> ids) throws ResourceNotFoundException {
		this.findAllPlantById(ids);
		plantRepo.deleteAllByIdInBatch(ids);
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

	private PlantResponse mapToPlantResponse(Plant plant) {
		return modelMapper.map(plant, PlantResponse.class);
	}

	private Plant findPlantById(Long plantId) throws ResourceNotFoundException {
		Helpers.validateId(plantId);
		Optional<Plant> plant = plantRepo.findById(plantId);
		if (plant.isEmpty()) {
			throw new ResourceNotFoundException(PLANT_NOT_FOUND_MESSAGE);
		}
		return plant.get();
	}

	private Plant findPlantByName(String name) throws ResourceNotFoundException {
		Optional<Plant> plant = plantRepo.findByPlantName(name);
		if (plant.isEmpty()) {
			throw new ResourceNotFoundException(PLANT_NOT_FOUND_MESSAGE);
		}
		return plant.get();
	}

	private List<Plant> findAllPlantById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<Plant> plants = plantRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> plants.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Plants with IDs " + missingIds + " not found.");
		}
		return plants;
	}
}
