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
import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageBin;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.StorageBinRepo;
import com.example.plantservice.repository.StorageLocationRepo;
import com.example.plantservice.service.interfaces.StorageBinService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageBinServiceImpl implements StorageBinService {
	private final ExcelFileHelper excelFileHelper;
	private final StorageBinRepo storageBinRepo;

	private final PlantRepo plantRepo;

	private final StorageLocationRepo storageLocationRepo;
	private final DynamicClient dynamicClient;

	private final ModelMapper modelMapper;

	@Override
	public StorageBinResponse saveStorageBin(StorageBinRequest storageBinRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(storageBinRequest);
		boolean exists = storageBinRepo.existsByStorageBinCodeAndStorageBinName(storageBinRequest.getStorageBinCode(),
				storageBinRequest.getStorageBinName());
		if (!exists) {
			StorageBin storageBin = modelMapper.map(storageBinRequest, StorageBin.class);
			for (Map.Entry<String, Object> entryField : storageBin.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = StorageBin.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			storageBin.setId(null);
			storageBin.setPlant(getPlantById(storageBinRequest.getPlantId()));
			storageBin.setStorageLocation(getStorageLocationById(storageBinRequest.getStorageLocationId()));
			StorageBin saved = storageBinRepo.save(storageBin);
			return mapToStorageBinResponse(saved);
		} else {
			throw new AlreadyExistsException("StorageLocationBin with this name already exists");
		}
	}

	@Override
	public StorageBinResponse getStorageBinById(Long id) throws ResourceNotFoundException {
		StorageBin storageBin = this.findStorageBinById(id);
		return mapToStorageBinResponse(storageBin);
	}

	@Override
	public List<StorageBinResponse> getAllStorageBin() {
		List<StorageBin> storageBin = storageBinRepo.findAllByOrderByIdAsc();
		return storageBin.stream().map(this::mapToStorageBinResponse).toList();
	}

	@Override
	public List<StorageBin> findAll() {
		return storageBinRepo.findAllByOrderByIdAsc();
	}

	@Override
	public StorageBinResponse updateStorageBin(Long id, StorageBinRequest storageBinRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(storageBinRequest);
		String existName = storageBinRequest.getStorageBinName();
		String existCode = storageBinRequest.getStorageBinCode();
		boolean exists = storageBinRepo.existsByStorageBinCodeAndStorageBinNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			StorageBin existStorageBin = this.findStorageBinById(id);
			if (!existStorageBin.getStorageBinCode().equals(existCode)) {
				auditFields
						.add(new AuditFields(null, "Storage Bin Code", existStorageBin.getStorageBinCode(), existCode));
				existStorageBin.setStorageBinCode(existCode);
			}
			if (!existStorageBin.getStorageBinName().equals(existName)) {
				auditFields
						.add(new AuditFields(null, "Storage Bin Name", existStorageBin.getStorageBinName(), existName));
				existStorageBin.setStorageBinName(existName);
			}
			if (!existStorageBin.getStorageBinStatus().equals(storageBinRequest.getStorageBinStatus())) {
				auditFields.add(new AuditFields(null, "Storage Bin Status", existStorageBin.getStorageBinStatus(),
						storageBinRequest.getStorageBinStatus()));
				existStorageBin.setStorageBinStatus(storageBinRequest.getStorageBinStatus());
			}
			if (!existStorageBin.getPlant().equals(getPlantById(storageBinRequest.getPlantId()))) {
				auditFields.add(
						new AuditFields(null, "Plant", existStorageBin.getPlant(), storageBinRequest.getPlantId()));
				existStorageBin.setPlant(getPlantById(storageBinRequest.getPlantId()));
			}
			if (!existStorageBin.getStorageLocation()
					.equals(getStorageLocationById(storageBinRequest.getStorageLocationId()))) {
				auditFields.add(new AuditFields(null, "Storage Location", existStorageBin.getStorageLocation(),
						storageBinRequest.getStorageLocationId()));
				existStorageBin.setStorageLocation(getStorageLocationById(storageBinRequest.getStorageLocationId()));
			}
			if (!existStorageBin.getDynamicFields().equals(storageBinRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : storageBinRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existStorageBin.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existStorageBin.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existStorageBin.updateAuditHistory(auditFields);
			storageBinRepo.save(existStorageBin);
			return mapToStorageBinResponse(existStorageBin);
		} else {
			throw new AlreadyExistsException("StorageBin with this name already exists");
		}
	}

	@Override
	public List<StorageBinResponse> updateBulkStatusStorageBinId(List<Long> id) throws ResourceNotFoundException {
		List<StorageBin> existingStorageBins = this.findAllBinsById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingStorageBins.forEach(existingStorageBin -> {
			if (existingStorageBin.getStorageBinStatus() != null) {
				auditFields.add(new AuditFields(null, "Storage Bin Status", existingStorageBin.getStorageBinStatus(),
						!existingStorageBin.getStorageBinStatus()));

				existingStorageBin.setStorageBinStatus(!existingStorageBin.getStorageBinStatus());
			}
			existingStorageBin.updateAuditHistory(auditFields);
		});
		storageBinRepo.saveAll(existingStorageBins);
		return existingStorageBins.stream().map(this::mapToStorageBinResponse).toList();
	}

	@Override
	public StorageBinResponse updateStatusUsingStorageBinId(Long id) throws ResourceNotFoundException {
		StorageBin existingStorageBin = this.findStorageBinById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingStorageBin.getStorageBinStatus() != null) {
			auditFields.add(new AuditFields(null, "Storage Bin Status", existingStorageBin.getStorageBinStatus(),
					!existingStorageBin.getStorageBinStatus()));

			existingStorageBin.setStorageBinStatus(!existingStorageBin.getStorageBinStatus());
		}
		existingStorageBin.updateAuditHistory(auditFields);
		storageBinRepo.save(existingStorageBin);
		return mapToStorageBinResponse(existingStorageBin);
	}

	@Override
	public void deleteStorageBin(Long id) throws ResourceNotFoundException {
		StorageBin storageBin = this.findStorageBinById(id);
		storageBinRepo.deleteById(storageBin.getId());
	}

	@Override
	public void deleteBatchStorageBin(List<Long> ids) throws ResourceNotFoundException {
		this.findAllBinsById(ids);
		storageBinRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "StorageBin";
		Class<?> clazz = StorageBinRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "StorageBin_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<StorageBin> data = excelFileHelper.readDataFromExcel(file.getInputStream(), StorageBin.class);
		for (StorageBin dataS : data) {
			if (!storageBinRepo.existsByStorageBinCodeAndStorageBinName(dataS.getStorageBinCode(),
					dataS.getStorageBinName())) {

				this.storageBinRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "StorageBin";
		Class<?> clazz = StorageBinResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "StorageBin_";
		List<StorageBinResponse> allValue = getAllStorageBin();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertBinListToMap(List<StorageBin> binList) {
		List<Map<String, Object>> data = new ArrayList<>();

		for (StorageBin sbin : binList) {
			Map<String, Object> binData = new HashMap<>();
			binData.put("Id", sbin.getId());
			binData.put("Code", sbin.getStorageBinCode());
			binData.put("Name", sbin.getStorageBinName());
			binData.put("Status", sbin.getStorageBinStatus());
			data.add(binData);
		}
		return data;
	}

	private Plant getPlantById(Long plantId) {
		Optional<Plant> fetchplantOptional = plantRepo.findById(plantId);
		return fetchplantOptional.orElse(null);

	}

	private StorageLocation getStorageLocationById(Long id) {
		Optional<StorageLocation> fetchStorageOptional1 = storageLocationRepo.findById(id);
		return fetchStorageOptional1.orElse(null);

	}

	private StorageBinResponse mapToStorageBinResponse(StorageBin storageLocation) {
		return modelMapper.map(storageLocation, StorageBinResponse.class);
	}

	private StorageBin findStorageBinById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<StorageBin> storageBin = storageBinRepo.findById(id);
		if (storageBin.isEmpty()) {
			throw new ResourceNotFoundException("Storage Bin with ID " + id + " not found");
		}
		return storageBin.get();
	}

	private List<StorageBin> findAllBinsById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<StorageBin> bins = findAllBinsById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> bins.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Storage Bin with IDs " + missingIds + " not found.");
		}
		return bins;
	}

}
