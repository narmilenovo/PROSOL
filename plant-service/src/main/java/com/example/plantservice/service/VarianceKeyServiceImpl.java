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
import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.VarianceKey;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.VarianceKeyMapper;
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
	private final VarianceKeyMapper varianceKeyMapper;
	private final DynamicClient dynamicClient;

	@Override
	public VarianceKeyResponse saveVarianceKey(VarianceKeyRequest valuationCategoryRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(valuationCategoryRequest);
		String varianceKeyCode = valuationCategoryRequest.getVarianceKeyCode();
		String varianceKeyName = valuationCategoryRequest.getVarianceKeyName();
		if (varianceKeyRepo.existsByVarianceKeyCodeAndVarianceKeyName(varianceKeyCode, varianceKeyName)) {
			throw new AlreadyExistsException("VarianceKey with this name already exists");
		}
		VarianceKey valuationCategory = varianceKeyMapper.mapToVarianceKey(valuationCategoryRequest);

		validateDynamicFields(valuationCategory);

		varianceKeyRepo.save(valuationCategory);
		return varianceKeyMapper.mapToVarianceKeyResponse(valuationCategory);
	}

	@Override
	public VarianceKeyResponse getVarianceKeyById(Long id) throws ResourceNotFoundException {
		VarianceKey valuationCategory = this.findVarianceKeyById(id);
		return varianceKeyMapper.mapToVarianceKeyResponse(valuationCategory);
	}

	@Override
	public List<VarianceKey> findAll() {
		return varianceKeyRepo.findAllByOrderByIdAsc();
	}

	@Override
	public List<VarianceKeyResponse> getAllVarianceKey() {
		return varianceKeyRepo.findAllByOrderByIdAsc().stream().map(varianceKeyMapper::mapToVarianceKeyResponse)
				.toList();
	}

	@Override
	public VarianceKeyResponse updateVarianceKey(Long id, VarianceKeyRequest varianceKeyRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
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
			return varianceKeyMapper.mapToVarianceKeyResponse(existingVarianceKey);
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
		return existingVarianceKeys.stream().map(varianceKeyMapper::mapToVarianceKeyResponse).toList();
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
		return varianceKeyMapper.mapToVarianceKeyResponse(existingVarianceKey);
	}

	public void deleteVarianceKey(Long id) throws ResourceNotFoundException {
		VarianceKey varianceKey = this.findVarianceKeyById(id);
		if (varianceKey != null) {
			varianceKeyRepo.delete(varianceKey);
		}
	}

	@Override
	public void deleteBatchVarianceKey(List<Long> ids) throws ResourceNotFoundException {
		List<VarianceKey> varianceKeys = this.findAllKeysById(ids);
		if (!varianceKeys.isEmpty()) {
			varianceKeyRepo.deleteAll(varianceKeys);
		}
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

	private void validateDynamicFields(VarianceKey valuationCategory) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : valuationCategory.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = VarianceKey.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private VarianceKey findVarianceKeyById(Long id) throws ResourceNotFoundException {
		return varianceKeyRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("VarianceKey with ID " + id + " not found"));
	}

	private List<VarianceKey> findAllKeysById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids); // Convert ids to a set for faster lookup
		List<VarianceKey> keys = varianceKeyRepo.findAllById(ids);

		List<VarianceKey> foundKeys = keys.stream().filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Variance Key with IDs " + missingIds + " not found.");
		}
		return foundKeys;
	}

}
