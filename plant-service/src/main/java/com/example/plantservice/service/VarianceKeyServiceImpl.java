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
import com.example.plantservice.entity.AuditFields;
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

	@Override
	public VarianceKeyResponse saveVarianceKey(VarianceKeyRequest valuationCategoryRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(valuationCategoryRequest);
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
		Helpers.inputTitleCase(varianceKeyRequest);
		String existName = varianceKeyRequest.getVarianceKeyName();
		String existCode = varianceKeyRequest.getVarianceKeyCode();
		boolean exists = varianceKeyRepo.existsByVarianceKeyCodeAndVarianceKeyNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			VarianceKey existingVarianceKey = this.findVarianceKeyById(id);
			if (!existingVarianceKey.getVarianceKeyCode().equals(existCode)) {
				auditFields.add(
						new AuditFields(null, "varianceKey Code", existingVarianceKey.getVarianceKeyCode(), existCode));
				existingVarianceKey.setVarianceKeyCode(existCode);
			}
			if (!existingVarianceKey.getVarianceKeyName().equals(existName)) {
				auditFields.add(
						new AuditFields(null, "varianceKey Name", existingVarianceKey.getVarianceKeyName(), existName));
				existingVarianceKey.setVarianceKeyName(existName);
			}
			if (!existingVarianceKey.getVarianceKeyStatus().equals(varianceKeyRequest.getVarianceKeyStatus())) {
				auditFields.add(new AuditFields(null, "varianceKey Status", existingVarianceKey.getVarianceKeyStatus(),
						varianceKeyRequest.getVarianceKeyStatus()));
				existingVarianceKey.setVarianceKeyStatus(varianceKeyRequest.getVarianceKeyStatus());
			}
			if (!existingVarianceKey.getDynamicFields().equals(varianceKeyRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : varianceKeyRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingVarianceKey.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingVarianceKey.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
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
		List<VarianceKey> existingVarianceKeys = this.findAllKeysById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingVarianceKeys.forEach(existingVarianceKey -> {
			if (existingVarianceKey.getVarianceKeyStatus() != null) {
				auditFields.add(new AuditFields(null, "varianceKey Status", existingVarianceKey.getVarianceKeyStatus(),
						!existingVarianceKey.getVarianceKeyStatus()));
				existingVarianceKey.setVarianceKeyStatus(!existingVarianceKey.getVarianceKeyStatus());
			}
			existingVarianceKey.updateAuditHistory(auditFields);
		});
		varianceKeyRepo.saveAll(existingVarianceKeys);
		return existingVarianceKeys.stream().map(this::mapToVarianceKeyResponse).toList();
	}

	@Override
	public VarianceKeyResponse updateStatusUsingVarianceKeyId(Long id) throws ResourceNotFoundException {
		VarianceKey existingVarianceKey = this.findVarianceKeyById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingVarianceKey.getVarianceKeyStatus() != null) {
			auditFields.add(new AuditFields(null, "varianceKey Status", existingVarianceKey.getVarianceKeyStatus(),
					!existingVarianceKey.getVarianceKeyStatus()));
			existingVarianceKey.setVarianceKeyStatus(!existingVarianceKey.getVarianceKeyStatus());
		}
		existingVarianceKey.updateAuditHistory(auditFields);
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
			throw new ResourceNotFoundException("VarianceKey with ID " + id + " not found");
		}
		return valuationCategory.get();
	}

	private List<VarianceKey> findAllKeysById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<VarianceKey> keys = varianceKeyRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> keys.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Variance Key with IDs " + missingIds + " not found.");
		}
		return keys;
	}

}
