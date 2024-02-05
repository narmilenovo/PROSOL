package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.StorageLocationRepo;
import com.example.plantservice.service.interfaces.StorageLocationService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {
	private static final String STORAGE_LOCATION_NOT_FOUND_MESSAGE = null;
	private final ExcelFileHelper excelFileHelper;
	private final StorageLocationRepo storageLocationRepo;

	private final PlantRepo plantRepo;
	private final DynamicClient dynamicClient;

	private final ModelMapper modelMapper = new ModelMapper();

	@Override
	public StorageLocationResponse saveStorageLocation(StorageLocationRequest storageLocationRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = storageLocationRepo.existsByStorageLocationCodeAndStorageLocationName(
				storageLocationRequest.getStorageLocationCode(), storageLocationRequest.getStorageLocationName());
		if (!exists) {
			StorageLocation storageLocation = modelMapper.map(storageLocationRequest, StorageLocation.class);
			for (Map.Entry<String, Object> entryField : storageLocation.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = StorageLocation.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			storageLocation.setId(null);
			Plant plant = this.getPlantByid(storageLocationRequest.getPlantId());
			storageLocation.setPlant(plant);
			StorageLocation savedLocation = storageLocationRepo.save(storageLocation);
			return mapToStorageLocationResponse(savedLocation);
		} else {
			throw new AlreadyExistsException("StorageLocation with this name already exists");
		}
	}

	@Override
	public StorageLocationResponse getStorageLocationById(Long id) throws ResourceNotFoundException {
		StorageLocation storageLocation = this.findStorageLocationById(id);
		return mapToStorageLocationResponse(storageLocation);
	}

	@Override
	public List<StorageLocationResponse> getAllByPlantByName(String name) {
		List<StorageLocation> storageLocations = storageLocationRepo.findByPlant_PlantName(name);
		return storageLocations.stream().sorted(Comparator.comparing(StorageLocation::getId))
				.map(this::mapToStorageLocationResponse).toList();
	}

	@Override
	public List<StorageLocationResponse> getAllByPlantById(Long id) {
		List<StorageLocation> storageLocations = storageLocationRepo.findByPlant_Id(id);
		return storageLocations.stream().sorted(Comparator.comparing(StorageLocation::getId))
				.map(this::mapToStorageLocationResponse).toList();
	}

	@Override
	public List<StorageLocationResponse> getAllStorageLocation() {
		List<StorageLocation> storageLocation = storageLocationRepo.findAllByOrderByIdAsc();
		return storageLocation.stream().map(this::mapToStorageLocationResponse).toList();
	}

	@Override
	public List<StorageLocation> findAll() {
		return storageLocationRepo.findAllByOrderByIdAsc();
	}

	@Override
	@Transactional
	public StorageLocationResponse updateStorageLocation(Long id, StorageLocationRequest storageLocationRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String existCode = storageLocationRequest.getStorageLocationCode();
		String existName = storageLocationRequest.getStorageLocationName();
		boolean exists = storageLocationRepo.existsByStorageLocationCodeAndStorageLocationNameAndIdNot(existCode,
				existName, id);
		if (!exists) {
			StorageLocation existingStorageLocation = this.findStorageLocationById(id);
			existingStorageLocation.setStorageLocationCode(storageLocationRequest.getStorageLocationCode());
			existingStorageLocation.setStorageLocationName(storageLocationRequest.getStorageLocationName());
			existingStorageLocation.setStorageLocationStatus(storageLocationRequest.getStorageLocationStatus());
			Plant plant = existingStorageLocation.getPlant();
			if (storageLocationRequest.getPlantId() != null) {
				plant = this.getPlantByid(storageLocationRequest.getPlantId());
				existingStorageLocation.setPlant(plant);
			}
			for (Map.Entry<String, Object> entryField : existingStorageLocation.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = StorageLocation.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			storageLocationRepo.save(existingStorageLocation);
			return mapToStorageLocationResponse(existingStorageLocation);
		} else {
			throw new AlreadyExistsException("StorageLocation with this name already exists");
		}
	}

	@Override
	public List<StorageLocationResponse> updateBulkStatusStorageLocationId(List<Long> id)
			throws ResourceNotFoundException {
		List<StorageLocation> existingStorageLocation = this.findAllStorLocById(id);
		for (StorageLocation storageLocation : existingStorageLocation) {
			storageLocation.setStorageLocationStatus(!storageLocation.getStorageLocationStatus());
		}
		storageLocationRepo.saveAll(existingStorageLocation);
		return existingStorageLocation.stream().map(this::mapToStorageLocationResponse).toList();
	}

	@Override
	public StorageLocationResponse updateStatusUsingStorageLocationId(Long id) throws ResourceNotFoundException {
		StorageLocation existingStorageLocation = this.findStorageLocationById(id);
		existingStorageLocation.setStorageLocationStatus(!existingStorageLocation.getStorageLocationStatus());
		storageLocationRepo.save(existingStorageLocation);
		return mapToStorageLocationResponse(existingStorageLocation);
	}

	@Override
	public void deleteStorageLocation(Long id) throws ResourceNotFoundException {
		StorageLocation storageLocation = this.findStorageLocationById(id);
		storageLocationRepo.deleteById(storageLocation.getId());
	}

	@Override
	public void deleteBatchStorageLocation(List<Long> ids) throws ResourceNotFoundException {
		this.findAllStorLocById(ids);
		storageLocationRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "StorageLocation";
		Class<?> clazz = StorageLocationRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "StorageLocation_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<StorageLocation> data = excelFileHelper.readDataFromExcel(file.getInputStream(), StorageLocation.class);
		for (StorageLocation dataS : data) {
			if (!storageLocationRepo.existsByStorageLocationCodeAndStorageLocationName(dataS.getStorageLocationCode(),
					dataS.getStorageLocationName())) {

				this.storageLocationRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "StorageLocation";
		Class<?> clazz = DepartmentResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "StorageLocation_";
		List<StorageLocationResponse> allValue = getAllStorageLocation();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertStorageLocationListToMap(List<StorageLocation> sLocList) {
		List<Map<String, Object>> data = new ArrayList<>();

		for (StorageLocation sLocs : sLocList) {
			Map<String, Object> locData = new HashMap<>();
			locData.put("Id", sLocs.getId());
			locData.put("Code", sLocs.getStorageLocationCode());
			locData.put("Name", sLocs.getStorageLocationName());
			locData.put("Status", sLocs.getStorageLocationStatus());
			data.add(locData);
		}
		return data;
	}

	private Plant getPlantByid(Long plantId) throws ResourceNotFoundException {
		Optional<Plant> fetchplantOptional = plantRepo.findById(plantId);
		return fetchplantOptional
				.orElseThrow(() -> new ResourceNotFoundException("Plant is not found with this id: " + plantId));
	}

	private StorageLocationResponse mapToStorageLocationResponse(StorageLocation storageLocation) {
		return modelMapper.map(storageLocation, StorageLocationResponse.class);
	}

	private StorageLocation findStorageLocationById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<StorageLocation> storageLocation = storageLocationRepo.findById(id);
		if (storageLocation.isEmpty()) {
			throw new ResourceNotFoundException(STORAGE_LOCATION_NOT_FOUND_MESSAGE);
		}
		return storageLocation.get();
	}

	private List<StorageLocation> findAllStorLocById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<StorageLocation> locations = storageLocationRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> locations.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return locations;
	}

}
