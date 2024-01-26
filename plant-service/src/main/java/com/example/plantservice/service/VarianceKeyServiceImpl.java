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
import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.entity.VarianceKey;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.VarianceKeyRepo;
import com.example.plantservice.service.interfaces.VarianceKeyService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VarianceKeyServiceImpl implements VarianceKeyService {

	private final VarianceKeyRepo varianceKeyRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String VARIANCE_KEY_NOT_FOUND_MESSAGE = null;

	@Override
	public VarianceKeyResponse saveVarianceKey(VarianceKeyRequest valuationCategoryRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = varianceKeyRepo.existsByVarianceKeyCodeAndVarianceKeyName(
				valuationCategoryRequest.getVarianceKeyCode(), valuationCategoryRequest.getVarianceKeyName());
		if (!exists) {
			VarianceKey valuationCategory = modelMapper.map(valuationCategoryRequest, VarianceKey.class);
			for (Map.Entry<String, Object> entryField : valuationCategory.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = VarianceKey.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			varianceKeyRepo.save(valuationCategory);
			return mapToVarianceKeyResponse(valuationCategory);
		} else {
			throw new AlreadyExistsException("VarianceKey with this name already exists");
		}
	}

	@Override
	public VarianceKeyResponse getVarianceKeyById(Long id) throws ResourceNotFoundException {
		VarianceKey valuationCategory = this.findVarianceKeyById(id);
		return mapToVarianceKeyResponse(valuationCategory);
	}

	@Override
	public List<VarianceKey> findAll() {
		return varianceKeyRepo.findAllByOrderByIdAsc();
	}

	@Override
	public List<VarianceKeyResponse> getAllVarianceKey() {
		List<VarianceKey> valuationCategory = varianceKeyRepo.findAllByOrderByIdAsc();
		return valuationCategory.stream().map(this::mapToVarianceKeyResponse).toList();
	}

	@Override
	public VarianceKeyResponse updateVarianceKey(Long id, VarianceKeyRequest varianceKeyRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String existName = varianceKeyRequest.getVarianceKeyName();
		String existCode = varianceKeyRequest.getVarianceKeyCode();
		boolean exists = varianceKeyRepo.existsByVarianceKeyCodeAndVarianceKeyNameAndIdNot(existCode, existName, id);
		if (!exists) {
			VarianceKey existingVarianceKey = this.findVarianceKeyById(id);
			modelMapper.map(varianceKeyRequest, existingVarianceKey);
			for (Map.Entry<String, Object> entryField : existingVarianceKey.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = VarianceKey.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			varianceKeyRepo.save(existingVarianceKey);
			return mapToVarianceKeyResponse(existingVarianceKey);
		} else {
			throw new AlreadyExistsException("VarianceKey with this name already exists");
		}
	}

	@Override
	public List<VarianceKeyResponse> updateBulkStatusVarianceKeyId(List<Long> id) throws ResourceNotFoundException {
		List<VarianceKey> existingVarianceKey = this.findAllKeysById(id);
		for (VarianceKey valuationCategory : existingVarianceKey) {
			valuationCategory.setVarianceKeyStatus(!valuationCategory.getVarianceKeyStatus());
		}
		varianceKeyRepo.saveAll(existingVarianceKey);
		return existingVarianceKey.stream().map(this::mapToVarianceKeyResponse).toList();
	}

	@Override
	public VarianceKeyResponse updateStatusUsingVarianceKeyId(Long id) throws ResourceNotFoundException {
		VarianceKey existingVarianceKey = this.findVarianceKeyById(id);
		existingVarianceKey.setVarianceKeyStatus(!existingVarianceKey.getVarianceKeyStatus());
		varianceKeyRepo.save(existingVarianceKey);
		return mapToVarianceKeyResponse(existingVarianceKey);
	}

	public void deleteVarianceKey(Long id) throws ResourceNotFoundException {
		VarianceKey valuationCategory = this.findVarianceKeyById(id);
		varianceKeyRepo.deleteById(valuationCategory.getId());
	}

	@Override
	public void deleteBatchVarianceKey(List<Long> ids) throws ResourceNotFoundException {
		this.findAllKeysById(ids);
		varianceKeyRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "VarianceKey";
		Class<?> clazz = VarianceKeyRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "VarianceKey_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<VarianceKey> data = excelFileHelper.readDataFromExcel(file.getInputStream(), VarianceKey.class);
		for (VarianceKey dataS : data) {
			if (!varianceKeyRepo.existsByVarianceKeyCodeAndVarianceKeyName(dataS.getVarianceKeyCode(),
					dataS.getVarianceKeyName())) {

				this.varianceKeyRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "VarianceKey";
		Class<?> clazz = VarianceKeyResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "VarianceKey_";
		List<VarianceKeyResponse> allValue = getAllVarianceKey();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertVarianceKeyListToMap(List<VarianceKey> varianceKeyList) {
		List<Map<String, Object>> data = new ArrayList<>();

		for (VarianceKey varianceKey : varianceKeyList) {
			Map<String, Object> varianceKeyData = new HashMap<>();
			varianceKeyData.put("Id", varianceKey.getId());
			varianceKeyData.put("Code", varianceKey.getVarianceKeyCode());
			varianceKeyData.put("Name", varianceKey.getVarianceKeyName());
			varianceKeyData.put("Status", varianceKey.getVarianceKeyStatus());
			data.add(varianceKeyData);
		}
		return data;
	}

	private VarianceKeyResponse mapToVarianceKeyResponse(VarianceKey valuationCategory) {
		return modelMapper.map(valuationCategory, VarianceKeyResponse.class);
	}

	private VarianceKey findVarianceKeyById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<VarianceKey> valuationCategory = varianceKeyRepo.findById(id);
		if (valuationCategory.isEmpty()) {
			throw new ResourceNotFoundException(VARIANCE_KEY_NOT_FOUND_MESSAGE);
		}
		return valuationCategory.get();
	}

	private List<VarianceKey> findAllKeysById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<VarianceKey> keys = varianceKeyRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> keys.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Variance Key with IDs " + missingIds + " not found.");
		}
		return keys;
	}

}
