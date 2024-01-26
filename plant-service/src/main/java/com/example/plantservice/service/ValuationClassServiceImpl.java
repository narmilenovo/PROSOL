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

import com.example.plantservice.client.ValuationMaterialResponse;
import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.client.General.MaterialTypeClient;
import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.entity.ValuationClass;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.ValuationClassRepo;
import com.example.plantservice.service.interfaces.ValuationClassService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValuationClassServiceImpl implements ValuationClassService {

	public static final String VALUATION_CLASS_NOT_FOUND_MESSAGE = null;
	private final ValuationClassRepo valuationClassRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;
	private final MaterialTypeClient materialTypeClient;

	@Override
	public ValuationClassResponse saveValuationClass(ValuationClassRequest valuationClassRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = valuationClassRepo.existsByValuationClassCodeAndValuationClassName(
				valuationClassRequest.getValuationClassCode(), valuationClassRequest.getValuationClassName());
		if (!exists) {
			ValuationClass valuationClass = modelMapper.map(valuationClassRequest, ValuationClass.class);
			for (Map.Entry<String, Object> entryField : valuationClass.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ValuationClass.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			valuationClass.setId(null);
			valuationClassRepo.save(valuationClass);
			return mapToValuationClassResponse(valuationClass);
		} else {
			throw new AlreadyExistsException("ValuationClass with this name already exists");
		}
	}

	@Override
	public ValuationClassResponse getValuationClassById(Long id) throws ResourceNotFoundException {
		ValuationClass valuationClass = this.findValuationClassById(id);
		return mapToValuationClassResponse(valuationClass);
	}

	@Override
	public ValuationMaterialResponse getValuationMaterialById(Long id) throws ResourceNotFoundException {
		ValuationClass valuationClass = this.findValuationClassById(id);
		return mapToValuationMaterialResponse(valuationClass);
	}

	@Override
	public List<ValuationClassResponse> getAllValuationClass() throws ResourceNotFoundException {
		List<ValuationClass> valuationClass = this.findAllValuationClass();
		return valuationClass.stream().map(this::mapToValuationClassResponse).toList();
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
		Helpers.validateId(id);
		String existName = valuationClassRequest.getValuationClassName();
		String existCode = valuationClassRequest.getValuationClassCode();
		boolean exists = valuationClassRepo.existsByValuationClassCodeAndValuationClassNameAndIdNot(existCode,
				existName, id);
		if (!exists) {
			ValuationClass existingValuationClass = this.findValuationClassById(id);
			modelMapper.map(valuationClassRequest, existingValuationClass);
			for (Map.Entry<String, Object> entryField : existingValuationClass.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ValuationClass.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			existingValuationClass.setId(id);
			valuationClassRepo.save(existingValuationClass);
			return mapToValuationClassResponse(existingValuationClass);

		} else {
			throw new AlreadyExistsException("ValuationClass with this name already exists");
		}
	}

	@Override
	public List<ValuationClassResponse> updateBulkStatusValuationClassId(List<Long> id)
			throws ResourceNotFoundException {
		List<ValuationClass> existingValuationClass = this.findAllValuationClassById(id);
		for (ValuationClass valuationClass : existingValuationClass) {
			valuationClass.setValuationClassStatus(!valuationClass.getValuationClassStatus());
		}
		valuationClassRepo.saveAll(existingValuationClass);
		return existingValuationClass.stream().map(this::mapToValuationClassResponse).toList();
	}

	@Override
	public ValuationClassResponse updateStatusUsingValuationClassId(Long id) throws ResourceNotFoundException {
		ValuationClass existingValuationClass = this.findValuationClassById(id);
		existingValuationClass.setValuationClassStatus(!existingValuationClass.getValuationClassStatus());
		valuationClassRepo.save(existingValuationClass);
		return mapToValuationClassResponse(existingValuationClass);
	}

	@Override
	public void deleteValuationClass(Long id) throws ResourceNotFoundException {
		ValuationClass valuationClass = this.findValuationClassById(id);
		valuationClassRepo.deleteById(valuationClass.getId());
	}

	@Override
	public void deleteBatchValuationClass(List<Long> ids) throws ResourceNotFoundException {
		this.findAllValuationClassById(ids);
		valuationClassRepo.deleteAllByIdInBatch(ids);
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

	private ValuationClassResponse mapToValuationClassResponse(ValuationClass valuationClass) {
		return modelMapper.map(valuationClass, ValuationClassResponse.class);
	}

	private ValuationClass findValuationClassById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ValuationClass> valuationClass = valuationClassRepo.findById(id);
		if (valuationClass.isEmpty()) {
			throw new ResourceNotFoundException(VALUATION_CLASS_NOT_FOUND_MESSAGE);
		}
		return valuationClass.get();
	}

	private List<ValuationClass> findAllValuationClassById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ValuationClass> classes = valuationClassRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> classes.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Valuation Class with IDs " + missingIds + " not found.");
		}
		return classes;
	}

	public List<ValuationClass> findAllValuationClass() throws ResourceNotFoundException {
		List<ValuationClass> valuationClasses = valuationClassRepo.findAllByOrderByIdAsc();
		if (valuationClasses.isEmpty()) {
			throw new ResourceNotFoundException(VALUATION_CLASS_NOT_FOUND_MESSAGE);
		}
		return valuationClasses;
	}

	private ValuationMaterialResponse mapToValuationMaterialResponse(ValuationClass valuationClass)
			throws ResourceNotFoundException {
		ValuationMaterialResponse valuationMaterialResponse = modelMapper.map(valuationClass,
				ValuationMaterialResponse.class);
		if (materialTypeClient == null) {
			throw new IllegalStateException("General service client not initiated");
		}
		valuationMaterialResponse.setMaterial(materialTypeClient.getMaterialById(valuationClass.getMaterialTypeId()));
		return valuationMaterialResponse;
	}

}
