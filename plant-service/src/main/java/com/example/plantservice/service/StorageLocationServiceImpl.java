package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.StorageLocationMapper;
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
	private final ExcelFileHelper excelFileHelper;
	private final StorageLocationRepo storageLocationRepo;

	private final PlantRepo plantRepo;
	private final DynamicClient dynamicClient;

	private final StorageLocationMapper storageLocationMapper;

	@Override
	public StorageLocationResponse saveStorageLocation(StorageLocationRequest storageLocationRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(storageLocationRequest);
		String storageLocationCode = storageLocationRequest.getStorageLocationCode();
		String storageLocationName = storageLocationRequest.getStorageLocationName();
		if (storageLocationRepo.existsByStorageLocationCodeAndStorageLocationName(storageLocationCode,
				storageLocationName)) {
			throw new AlreadyExistsException("StorageLocation with this name already exists");
		}
		StorageLocation storageLocation = storageLocationMapper.mapToStorageLocation(storageLocationRequest);

		validateDynamicFields(storageLocation);

		storageLocation.setId(null);
		Plant plant = this.getPlantById(storageLocationRequest.getPlantId());
		storageLocation.setPlant(plant);
		StorageLocation savedLocation = storageLocationRepo.save(storageLocation);
		return storageLocationMapper.mapToStorageLocationResponse(savedLocation);
	}

	@Override
	public StorageLocationResponse getStorageLocationById(Long id) throws ResourceNotFoundException {
		StorageLocation storageLocation = this.findStorageLocationById(id);
		return storageLocationMapper.mapToStorageLocationResponse(storageLocation);
	}

	@Override
	public List<StorageLocationResponse> getAllByPlantByName(String name) {
		return storageLocationRepo.findByPlant_PlantName(name).stream()
				.sorted(Comparator.comparing(StorageLocation::getId))
				.map(storageLocationMapper::mapToStorageLocationResponse).toList();
	}

	@Override
	public List<StorageLocationResponse> getAllByPlantById(Long id) {
		return storageLocationRepo.findByPlant_Id(id).stream().sorted(Comparator.comparing(StorageLocation::getId))
				.map(storageLocationMapper::mapToStorageLocationResponse).toList();
	}

	@Override
	public List<StorageLocationResponse> getAllStorageLocation() {
		return storageLocationRepo.findAllByOrderByIdAsc().stream()
				.map(storageLocationMapper::mapToStorageLocationResponse).toList();
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
		Helpers.inputTitleCase(storageLocationRequest);

		String existCode = storageLocationRequest.getStorageLocationCode();
		String existName = storageLocationRequest.getStorageLocationName();
		boolean exists = storageLocationRepo.existsByStorageLocationCodeAndStorageLocationNameAndIdNot(existCode,
				existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			StorageLocation existingStorageLocation = this.findStorageLocationById(id);
			if (!existingStorageLocation.getStorageLocationCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "StorageLocation Code",
						existingStorageLocation.getStorageLocationCode(), existCode));
				existingStorageLocation.setStorageLocationCode(existCode);
			}
			if (!existingStorageLocation.getStorageLocationName().equals(existName)) {
				auditFields.add(new AuditFields(null, "StorageLocation Name",
						existingStorageLocation.getStorageLocationName(), existName));
				existingStorageLocation.setStorageLocationName(existName);
			}
			if (!existingStorageLocation.getStorageLocationStatus()
					.equals(storageLocationRequest.getStorageLocationStatus())) {
				auditFields.add(new AuditFields(null, "StorageLocation Status",
						existingStorageLocation.getStorageLocationStatus(),
						storageLocationRequest.getStorageLocationStatus()));
				existingStorageLocation.setStorageLocationStatus(storageLocationRequest.getStorageLocationStatus());
			}
			Plant existingPlant = existingStorageLocation.getPlant();
			if (!existingPlant.equals(getPlantById(storageLocationRequest.getPlantId()))) {
				auditFields.add(new AuditFields(null, "Plant", existingPlant, storageLocationRequest.getPlantId()));
				existingPlant = this.getPlantById(storageLocationRequest.getPlantId());
				existingStorageLocation.setPlant(existingPlant);
			}
			if (!existingPlant.getDynamicFields().equals(storageLocationRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : storageLocationRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingPlant.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingPlant.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingStorageLocation.updateAuditHistory(auditFields);
			storageLocationRepo.save(existingStorageLocation);
			return storageLocationMapper.mapToStorageLocationResponse(existingStorageLocation);
		} else {
			throw new AlreadyExistsException("StorageLocation with this name already exists");
		}
	}

	@Override
	public List<StorageLocationResponse> updateBulkStatusStorageLocationId(List<Long> id)
			throws ResourceNotFoundException {
		List<StorageLocation> existingStorageLocations = this.findAllStorLocById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingStorageLocations.forEach(existingStorageLocation -> {
			if (existingStorageLocation.getStorageLocationStatus() != null) {
				auditFields.add(new AuditFields(null, "StorageLocation Status",
						existingStorageLocation.getStorageLocationStatus(),
						!existingStorageLocation.getStorageLocationStatus()));
				existingStorageLocation.setStorageLocationStatus(!existingStorageLocation.getStorageLocationStatus());
			}
			existingStorageLocation.updateAuditHistory(auditFields);
		});
		storageLocationRepo.saveAll(existingStorageLocations);
		return existingStorageLocations.stream().map(storageLocationMapper::mapToStorageLocationResponse).toList();
	}

	@Override
	public StorageLocationResponse updateStatusUsingStorageLocationId(Long id) throws ResourceNotFoundException {
		StorageLocation existingStorageLocation = this.findStorageLocationById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingStorageLocation.getStorageLocationStatus() != null) {
			auditFields.add(
					new AuditFields(null, "StorageLocation Status", existingStorageLocation.getStorageLocationStatus(),
							!existingStorageLocation.getStorageLocationStatus()));
			existingStorageLocation.setStorageLocationStatus(!existingStorageLocation.getStorageLocationStatus());
		}
		existingStorageLocation.updateAuditHistory(auditFields);
		storageLocationRepo.save(existingStorageLocation);
		return storageLocationMapper.mapToStorageLocationResponse(existingStorageLocation);
	}

	@Override
	public void deleteStorageLocation(Long id) throws ResourceNotFoundException {
		StorageLocation storageLocation = this.findStorageLocationById(id);
		if (storageLocation != null) {
			storageLocationRepo.delete(storageLocation);
		}
	}

	@Override
	public void deleteBatchStorageLocation(List<Long> ids) throws ResourceNotFoundException {
		List<StorageLocation> storageLocations = this.findAllStorLocById(ids);
		if (!storageLocations.isEmpty()) {
			storageLocationRepo.deleteAll(storageLocations);
		}
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

	private void validateDynamicFields(StorageLocation storageLocation) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : storageLocation.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = StorageLocation.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private Plant getPlantById(Long plantId) throws ResourceNotFoundException {
		return plantRepo.findById(plantId)
				.orElseThrow(() -> new ResourceNotFoundException("Plant is not found with this id: " + plantId));
	}

	private StorageLocation findStorageLocationById(Long id) throws ResourceNotFoundException {
		return storageLocationRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("StorageLocation with ID " + id + " not found"));
	}

	private List<StorageLocation> findAllStorLocById(List<Long> ids) throws ResourceNotFoundException {
		List<StorageLocation> locations = storageLocationRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);
		List<StorageLocation> foundLocations = locations.stream().filter(entity -> idSet.contains(entity.getId()))
				.toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Storage Location with IDs " + missingIds + " not found.");
		}
		return foundLocations;
	}

}
