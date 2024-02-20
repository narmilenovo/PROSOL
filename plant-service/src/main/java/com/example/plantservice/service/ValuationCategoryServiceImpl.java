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
import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.ValuationCategory;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.ValuationCategoryRepo;
import com.example.plantservice.service.interfaces.ValuationCategoryService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValuationCategoryServiceImpl implements ValuationCategoryService {

	private final ValuationCategoryRepo valuationCategoryRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public ValuationCategoryResponse saveValuationCategory(ValuationCategoryRequest valuationCategoryRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(valuationCategoryRequest);
		boolean exists = valuationCategoryRepo.existsByValuationCategoryCodeAndValuationCategoryName(
				valuationCategoryRequest.getValuationCategoryCode(),
				valuationCategoryRequest.getValuationCategoryName());
		if (!exists) {
			ValuationCategory valuationCategory = modelMapper.map(valuationCategoryRequest, ValuationCategory.class);
			for (Map.Entry<String, Object> entryField : valuationCategory.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ValuationCategory.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			valuationCategoryRepo.save(valuationCategory);
			return mapToValuationCategoryResponse(valuationCategory);
		} else {
			throw new AlreadyExistsException("ValuationCategory with this name already exists");
		}
	}

	@Override
	public ValuationCategoryResponse getValuationCategoryById(Long id) throws ResourceNotFoundException {
		ValuationCategory valuationCategory = this.findValuationCategoryById(id);
		return mapToValuationCategoryResponse(valuationCategory);
	}

	@Override
	public List<ValuationCategoryResponse> getAllValuationCategory() {
		List<ValuationCategory> valuationCategory = valuationCategoryRepo.findAllByOrderByIdAsc();
		return valuationCategory.stream().map(this::mapToValuationCategoryResponse).toList();
	}

	@Override
	public List<ValuationCategory> findAll() {
		return valuationCategoryRepo.findAllByOrderByIdAsc();
	}

	@Override
	public ValuationCategoryResponse updateValuationCategory(Long id, ValuationCategoryRequest valuationCategoryRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(valuationCategoryRequest);
		String existName = valuationCategoryRequest.getValuationCategoryName();
		String existCode = valuationCategoryRequest.getValuationCategoryCode();
		boolean exists = valuationCategoryRepo.existsByValuationCategoryCodeAndValuationCategoryNameAndIdNot(existCode,
				existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			ValuationCategory existingValuationCategory = this.findValuationCategoryById(id);
			if (!existingValuationCategory.getValuationCategoryCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "ValuationCategory Code",
						existingValuationCategory.getValuationCategoryCode(), existCode));
				existingValuationCategory.setValuationCategoryCode(existCode);
			}
			if (!existingValuationCategory.getValuationCategoryName().equals(existName)) {
				auditFields.add(new AuditFields(null, "ValuationCategory Name",
						existingValuationCategory.getValuationCategoryName(), existName));
				existingValuationCategory.setValuationCategoryName(existName);
			}
			if (!existingValuationCategory.getValuationCategoryStatus()
					.equals(valuationCategoryRequest.getValuationCategoryStatus())) {
				auditFields.add(new AuditFields(null, "ValuationCategory Status",
						existingValuationCategory.getValuationCategoryStatus(),
						valuationCategoryRequest.getValuationCategoryStatus()));
				existingValuationCategory
						.setValuationCategoryStatus(valuationCategoryRequest.getValuationCategoryStatus());
			}
			if (!existingValuationCategory.getDynamicFields().equals(valuationCategoryRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : valuationCategoryRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingValuationCategory.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingValuationCategory.getDynamicFields().put(fieldName, newValue); // Update the
																								// dynamicField // field
					}
				}
			}
			valuationCategoryRepo.save(existingValuationCategory);
			return mapToValuationCategoryResponse(existingValuationCategory);
		} else {
			throw new AlreadyExistsException("ValuationCategory with this name already exists");
		}
	}

	@Override
	public List<ValuationCategoryResponse> updateBulkStatusValuationCategoryId(List<Long> id)
			throws ResourceNotFoundException {
		List<ValuationCategory> existingValuationCategories = this.findAllValCatById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingValuationCategories.forEach(existingValuationCategory -> {
			if (existingValuationCategory.getValuationCategoryStatus() != null) {
				auditFields.add(new AuditFields(null, "ValuationCategory Status",
						existingValuationCategory.getValuationCategoryStatus(),
						!existingValuationCategory.getValuationCategoryStatus()));
				existingValuationCategory
						.setValuationCategoryStatus(!existingValuationCategory.getValuationCategoryStatus());
			}
			existingValuationCategory.updateAuditHistory(auditFields);
		});
		valuationCategoryRepo.saveAll(existingValuationCategories);
		return existingValuationCategories.stream().map(this::mapToValuationCategoryResponse).toList();
	}

	@Override
	public ValuationCategoryResponse updateStatusUsingValuationCategoryId(Long id) throws ResourceNotFoundException {
		ValuationCategory existingValuationCategory = this.findValuationCategoryById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingValuationCategory.getValuationCategoryStatus() != null) {
			auditFields.add(new AuditFields(null, "ValuationCategory Status",
					existingValuationCategory.getValuationCategoryStatus(),
					!existingValuationCategory.getValuationCategoryStatus()));
			existingValuationCategory
					.setValuationCategoryStatus(!existingValuationCategory.getValuationCategoryStatus());
		}
		existingValuationCategory.updateAuditHistory(auditFields);
		valuationCategoryRepo.save(existingValuationCategory);
		return mapToValuationCategoryResponse(existingValuationCategory);
	}

	public void deleteValuationCategory(Long id) throws ResourceNotFoundException {
		ValuationCategory valuationCategory = this.findValuationCategoryById(id);
		valuationCategoryRepo.deleteById(valuationCategory.getId());
	}

	@Override
	public void deleteBatchValuationCategory(List<Long> ids) throws ResourceNotFoundException {
		this.findAllValCatById(ids);
		valuationCategoryRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ValuationCategory";
		Class<?> clazz = ValuationCategoryRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ValuationCategory_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<ValuationCategory> data = excelFileHelper.readDataFromExcel(file.getInputStream(),
				ValuationCategory.class);
		for (ValuationCategory dataS : data) {
			if (!valuationCategoryRepo.existsByValuationCategoryCodeAndValuationCategoryName(
					dataS.getValuationCategoryCode(), dataS.getValuationCategoryName())) {

				this.valuationCategoryRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "ValuationCategory";
		Class<?> clazz = ValuationCategoryResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ValuationCategory_";
		List<ValuationCategoryResponse> allValue = getAllValuationCategory();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertValuationCategoryListToMap(List<ValuationCategory> valuationCategoryList) {
		List<Map<String, Object>> data = new ArrayList<>();

		for (ValuationCategory valuationCategory : valuationCategoryList) {
			Map<String, Object> valuationCategoryData = new HashMap<>();
			valuationCategoryData.put("Id", valuationCategory.getId());
			valuationCategoryData.put("Code", valuationCategory.getValuationCategoryCode());
			valuationCategoryData.put("Name", valuationCategory.getValuationCategoryName());
			valuationCategoryData.put("Status", valuationCategory.getValuationCategoryStatus());
			data.add(valuationCategoryData);
		}
		return data;
	}

	private ValuationCategoryResponse mapToValuationCategoryResponse(ValuationCategory valuationCategory) {
		return modelMapper.map(valuationCategory, ValuationCategoryResponse.class);
	}

	private ValuationCategory findValuationCategoryById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ValuationCategory> valuationCategory = valuationCategoryRepo.findById(id);
		if (valuationCategory.isEmpty()) {
			throw new ResourceNotFoundException("ValuationCategory with ID " + id + " not found");
		}
		return valuationCategory.get();
	}

	private List<ValuationCategory> findAllValCatById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ValuationCategory> categories = valuationCategoryRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> categories.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return categories;
	}

}
