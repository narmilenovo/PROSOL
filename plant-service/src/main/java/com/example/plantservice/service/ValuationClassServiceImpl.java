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

import com.example.plantservice.client.ValuationMaterialResponse;
import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.client.General.GeneralServiceClient;
import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.ValuationClass;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.ValuationClassMapper;
import com.example.plantservice.repository.ValuationClassRepo;
import com.example.plantservice.service.interfaces.ValuationClassService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValuationClassServiceImpl implements ValuationClassService {

	private final ValuationClassRepo valuationClassRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ValuationClassMapper valuationClassMapper;
	private final DynamicClient dynamicClient;
	private final GeneralServiceClient materialTypeClient;

	@Override
	public ValuationClassResponse saveValuationClass(ValuationClassRequest valuationClassRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(valuationClassRequest);
		String valuationClassCode = valuationClassRequest.getValuationClassCode();
		String valuationClassName = valuationClassRequest.getValuationClassName();
		if (valuationClassRepo.existsByValuationClassCodeAndValuationClassName(valuationClassCode,
				valuationClassName)) {
			throw new AlreadyExistsException("ValuationClass with this name already exists");
		}
		ValuationClass valuationClass = valuationClassMapper.mapToValuationClass(valuationClassRequest);
		validateDynamicFields(valuationClass);
		valuationClass.setId(null);
		valuationClassRepo.save(valuationClass);
		return valuationClassMapper.mapToValuationClassResponse(valuationClass);
	}

	@Override
	public ValuationClassResponse getValuationClassById(Long id) throws ResourceNotFoundException {
		ValuationClass valuationClass = this.findValuationClassById(id);
		return valuationClassMapper.mapToValuationClassResponse(valuationClass);
	}

	@Override
	public ValuationMaterialResponse getValuationMaterialById(Long id) throws ResourceNotFoundException {
		ValuationClass valuationClass = this.findValuationClassById(id);
		return mapToValuationMaterialResponse(valuationClass);
	}

	@Override
	public List<ValuationClassResponse> getAllValuationClass() throws ResourceNotFoundException {
		List<ValuationClass> valuationClass = this.findAllValuationClass();
		return valuationClass.stream().map(valuationClassMapper::mapToValuationClassResponse).toList();
	}

	@Override
	public List<ValuationMaterialResponse> getAllValuationClassByMaterial() throws ResourceNotFoundException {
		List<ValuationClass> valuationClasses = this.findAllValuationClass();
		List<ValuationMaterialResponse> responseList = new ArrayList<>();
		for (ValuationClass valuationClass : valuationClasses) {
			ValuationMaterialResponse valuationMaterialResponse = mapToValuationMaterialResponse(valuationClass);
			responseList.add(valuationMaterialResponse);
		}
		return responseList;
	}

	@Override
	public ValuationClassResponse updateValuationClass(Long id, ValuationClassRequest valuationClassRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(valuationClassRequest);
		String existName = valuationClassRequest.getValuationClassName();
		String existCode = valuationClassRequest.getValuationClassCode();
		boolean exists = valuationClassRepo.existsByValuationClassCodeAndValuationClassNameAndIdNot(existCode,
				existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			ValuationClass existingValuationClass = this.findValuationClassById(id);
			if (!existingValuationClass.getValuationClassCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "ValuationClass Code",
						existingValuationClass.getValuationClassCode(), existCode));
				existingValuationClass.setValuationClassCode(existCode);

			}
			if (!existingValuationClass.getValuationClassName().equals(existName)) {
				existingValuationClass.setValuationClassName(existName);
				auditFields.add(new AuditFields(null, "ValuationClass Name",
						existingValuationClass.getValuationClassName(), existName));
			}
			if (!existingValuationClass.getMaterialTypeId().equals(valuationClassRequest.getMaterialTypeId())) {
				auditFields.add(new AuditFields(null, "Material Type", existingValuationClass.getMaterialTypeId(),
						valuationClassRequest.getMaterialTypeId()));
				existingValuationClass.setMaterialTypeId(valuationClassRequest.getMaterialTypeId());
			}
			if (!existingValuationClass.getValuationClassStatus()
					.equals(valuationClassRequest.getValuationClassStatus())) {
				auditFields.add(
						new AuditFields(null, "ValuationClass Status", existingValuationClass.getValuationClassStatus(),
								valuationClassRequest.getValuationClassStatus()));
				existingValuationClass.setValuationClassStatus(valuationClassRequest.getValuationClassStatus());
			}
			if (!existingValuationClass.getDynamicFields().equals(valuationClassRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : valuationClassRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingValuationClass.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingValuationClass.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingValuationClass.updateAuditHistory(auditFields);
			valuationClassRepo.save(existingValuationClass);
			return valuationClassMapper.mapToValuationClassResponse(existingValuationClass);

		} else {
			throw new AlreadyExistsException("ValuationClass with this name already exists");
		}
	}

	@Override
	public List<ValuationClassResponse> updateBulkStatusValuationClassId(List<Long> id)
			throws ResourceNotFoundException {
		List<ValuationClass> existingValuationClasses = this.findAllValuationClassById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingValuationClasses.forEach(existingValuationClass -> {
			if (existingValuationClass.getValuationClassStatus() != null) {
				auditFields.add(
						new AuditFields(null, "ValuationClass Status", existingValuationClass.getValuationClassStatus(),
								!existingValuationClass.getValuationClassStatus()));
				existingValuationClass.setValuationClassStatus(!existingValuationClass.getValuationClassStatus());
			}
			existingValuationClass.updateAuditHistory(auditFields);
		});
		valuationClassRepo.saveAll(existingValuationClasses);
		return existingValuationClasses.stream().map(valuationClassMapper::mapToValuationClassResponse).toList();
	}

	@Override
	public ValuationClassResponse updateStatusUsingValuationClassId(Long id) throws ResourceNotFoundException {
		ValuationClass existingValuationClass = this.findValuationClassById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingValuationClass.getValuationClassStatus() != null) {
			auditFields.add(
					new AuditFields(null, "ValuationClass Status", existingValuationClass.getValuationClassStatus(),
							!existingValuationClass.getValuationClassStatus()));
			existingValuationClass.setValuationClassStatus(!existingValuationClass.getValuationClassStatus());
		}
		existingValuationClass.updateAuditHistory(auditFields);
		valuationClassRepo.save(existingValuationClass);
		return valuationClassMapper.mapToValuationClassResponse(existingValuationClass);
	}

	@Override
	public void deleteValuationClass(Long id) throws ResourceNotFoundException {
		ValuationClass valuationClass = this.findValuationClassById(id);
		if (valuationClass != null) {
			valuationClassRepo.delete(valuationClass);
		}
	}

	@Override
	public void deleteBatchValuationClass(List<Long> ids) throws ResourceNotFoundException {
		List<ValuationClass> valuationClasses = this.findAllValuationClassById(ids);
		if (!valuationClasses.isEmpty()) {
			valuationClassRepo.deleteAll(valuationClasses);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ValuationClass";
		Class<?> clazz = ValuationClassRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ValuationClass_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<ValuationClass> data = excelFileHelper.readDataFromExcel(file.getInputStream(), ValuationClass.class);
		for (ValuationClass dataS : data) {
			if (!valuationClassRepo.existsByValuationClassCodeAndValuationClassName(dataS.getValuationClassCode(),
					dataS.getValuationClassName())) {

				this.valuationClassRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException {
		String sheetName = "ValuationClass";
		Class<?> clazz = DepartmentResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ValuationClass_";
		List<ValuationClassResponse> allValue = getAllValuationClass();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertValuationClassListToMap(List<ValuationClass> valuationClassList) {
		List<Map<String, Object>> data = new ArrayList<>();

		for (ValuationClass valuationClass : valuationClassList) {
			Map<String, Object> valuationClassData = new HashMap<>();
			valuationClassData.put("Id", valuationClass.getId());
			valuationClassData.put("Code", valuationClass.getValuationClassCode());
			valuationClassData.put("Name", valuationClass.getValuationClassName());
			valuationClassData.put("Status", valuationClass.getValuationClassStatus());
			data.add(valuationClassData);
		}
		return data;
	}

	public List<ValuationClass> findAllValuationClass() throws ResourceNotFoundException {
		List<ValuationClass> valuationClasses = valuationClassRepo.findAllByOrderByIdAsc();
		if (valuationClasses.isEmpty()) {
			throw new ResourceNotFoundException("Valuation Class data is Empty in Db");
		}
		return valuationClasses;
	}

	private void validateDynamicFields(ValuationClass valuationClass) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : valuationClass.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = ValuationClass.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private ValuationClass findValuationClassById(Long id) throws ResourceNotFoundException {
		return valuationClassRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Valuation Class with ID " + id + " not found."));
	}

	private List<ValuationClass> findAllValuationClassById(List<Long> ids) throws ResourceNotFoundException {
		List<ValuationClass> classes = valuationClassRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);
		List<ValuationClass> foundClasses = classes.stream().filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Valuation Class with IDs " + missingIds + " not found.");
		}
		return foundClasses;
	}

	private ValuationMaterialResponse mapToValuationMaterialResponse(ValuationClass valuationClass)
			throws ResourceNotFoundException {
		ValuationMaterialResponse valuationMaterialResponse = valuationClassMapper
				.mapToValuationMaterialResponse(valuationClass);
		valuationMaterialResponse
				.setMaterialType(materialTypeClient.getMaterialById(valuationClass.getMaterialTypeId()));
		return valuationMaterialResponse;
	}

}
