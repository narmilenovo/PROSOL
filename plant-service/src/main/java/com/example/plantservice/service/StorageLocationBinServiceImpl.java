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
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageBin;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.StorageLocationBinRepo;
import com.example.plantservice.repository.StorageLocationRepo;
import com.example.plantservice.service.interfaces.StorageLocationBinService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageLocationBinServiceImpl implements StorageLocationBinService {
	private static final String STORAGE_BIN_NOT_FOUND_MESSAGE = null;
	private final ExcelFileHelper excelFileHelper;
	private final StorageLocationBinRepo storageLocationBinRepo;

	private final PlantRepo plantRepo;

	private final StorageLocationRepo storageLocationRepo;
	private final DynamicClient dynamicClient;

	private final ModelMapper modelMapper = new ModelMapper();

	@Override
	public StorageBinResponse saveStorageLocation(StorageBinRequest storageBinRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = storageLocationBinRepo.existsByStorageBinCodeAndStorageBinName(
				storageBinRequest.getStorageBinCode(), storageBinRequest.getStorageBinName());
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
			StorageBin saved = storageLocationBinRepo.save(storageBin);
			return mapToStorageBinResponse(saved);
		} else {
			throw new AlreadyExistsException("StorageLocationBin with this name already exists");
		}
	}

	@Override
	public StorageBinResponse getStorageLocationBinById(Long id) throws ResourceNotFoundException {
		StorageBin storageBin = this.findStorageBinById(id);
		return mapToStorageBinResponse(storageBin);
	}

	@Override
	public List<StorageBinResponse> getAllStorageLocationBin() {
		List<StorageBin> storageLocationBin = storageLocationBinRepo.findAllByOrderByIdAsc();
		return storageLocationBin.stream().map(this::mapToStorageBinResponse).toList();
	}

	@Override
	public List<StorageBin> findAll() {
		return storageLocationBinRepo.findAllByOrderByIdAsc();
	}

	@Override
	public StorageBinResponse updateStorageLocationBin(Long id, StorageBinRequest storageBinRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String existName = storageBinRequest.getStorageBinName();
		String existCode = storageBinRequest.getStorageBinCode();
		boolean exists = storageLocationBinRepo.existsByStorageBinCodeAndStorageBinNameAndIdNot(existCode, existName,
				id);
		if (!exists) {
			StorageBin existStorageLocationBin = this.findStorageBinById(id);
			existStorageLocationBin.setStorageBinCode(storageBinRequest.getStorageBinCode());
			existStorageLocationBin.setStorageBinName(storageBinRequest.getStorageBinName());
			existStorageLocationBin.setStorageBinStatus(storageBinRequest.getStorageBinStatus());
			existStorageLocationBin.setPlant(getPlantById(storageBinRequest.getPlantId()));
			existStorageLocationBin
					.setStorageLocation(getStorageLocationById(storageBinRequest.getStorageLocationId()));
			for (Map.Entry<String, Object> entryField : existStorageLocationBin.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = StorageBin.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			storageLocationBinRepo.save(existStorageLocationBin);
			return mapToStorageBinResponse(existStorageLocationBin);
		} else {
			throw new AlreadyExistsException("StorageLocationBin with this name already exists");
		}
	}

	@Override
	public List<StorageBinResponse> updateBulkStatusStorageLocationBinId(List<Long> id)
			throws ResourceNotFoundException {
		List<StorageBin> existingStorageLocationBin = this.findAllBinsById(id);
		for (StorageBin storageLocationBin : existingStorageLocationBin) {
			storageLocationBin.setStorageBinStatus(!storageLocationBin.getStorageBinStatus());
		}
		storageLocationBinRepo.saveAll(existingStorageLocationBin);
		return existingStorageLocationBin.stream().map(this::mapToStorageBinResponse).toList();
	}

	@Override
	public StorageBinResponse updateStatusUsingStorageLocationBinId(Long id) throws ResourceNotFoundException {
		StorageBin existingStorageBin = this.findStorageBinById(id);
		existingStorageBin.setStorageBinStatus(!existingStorageBin.getStorageBinStatus());
		storageLocationBinRepo.save(existingStorageBin);
		return mapToStorageBinResponse(existingStorageBin);
	}

	@Override
	public void deleteStorageLocationBin(Long id) throws ResourceNotFoundException {
		StorageBin storageBin = this.findStorageBinById(id);
		storageLocationBinRepo.deleteById(storageBin.getId());
	}

	@Override
	public void deleteBatchStorageLocationBin(List<Long> ids) throws ResourceNotFoundException {
		this.findAllBinsById(ids);
		storageLocationBinRepo.deleteAllByIdInBatch(ids);
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
			if (!storageLocationBinRepo.existsByStorageBinCodeAndStorageBinName(dataS.getStorageBinCode(),
					dataS.getStorageBinName())) {

				this.storageLocationBinRepo.save(dataS);
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
		List<StorageBinResponse> allValue = getAllStorageLocationBin();
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
		Optional<StorageBin> storageBin = storageLocationBinRepo.findById(id);
		if (storageBin.isEmpty()) {
			throw new ResourceNotFoundException(STORAGE_BIN_NOT_FOUND_MESSAGE);
		}
		return storageBin.get();
	}

	private List<StorageBin> findAllBinsById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<StorageBin> bins = findAllBinsById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> bins.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Storage Bin with IDs " + missingIds + " not found.");
		}
		return bins;
	}

}
