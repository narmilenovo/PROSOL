package com.example.mrpdataservice.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.client.Dynamic.DynamicClient;
import com.example.mrpdataservice.entity.AuditFields;
import com.example.mrpdataservice.entity.ProcurementType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.mapping.ProcurementTypeMapper;
import com.example.mrpdataservice.repository.ProcurementTypeRepo;
import com.example.mrpdataservice.request.ProcurementTypeRequest;
import com.example.mrpdataservice.response.ProcurementTypeResponse;
import com.example.mrpdataservice.service.ProcurementTypeService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcurementTypeServiceImpl implements ProcurementTypeService {

	private final ProcurementTypeRepo procurementTypeRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ProcurementTypeMapper procurementTypeMapper;
	private final DynamicClient dynamicClient;

	@Override
	public ProcurementTypeResponse saveProcurementType(ProcurementTypeRequest procurementTypeRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(procurementTypeRequest);
		String procurementTypeCode = procurementTypeRequest.getProcurementTypeCode();
		String procurementTypeName = procurementTypeRequest.getProcurementTypeName();
		if (procurementTypeRepo.existsByProcurementTypeCodeAndProcurementTypeName(procurementTypeCode,
				procurementTypeName)) {
			throw new AlreadyExistsException("ProcurementType with this name already exists");
		}
		ProcurementType procurementType = procurementTypeMapper.mapToProcurementType(procurementTypeRequest);
		validateDynamicFields(procurementType);
		procurementTypeRepo.save(procurementType);
		return procurementTypeMapper.mapToProcurementTypeResponse(procurementType);
	}

	@Override
	public ProcurementTypeResponse getProcurementTypeById(Long id) throws ResourceNotFoundException {
		ProcurementType procurementType = this.findProcurementTypeById(id);
		return procurementTypeMapper.mapToProcurementTypeResponse(procurementType);
	}

	@Override
	public List<ProcurementTypeResponse> getAllProcurementType() {
		List<ProcurementType> procurementType = procurementTypeRepo.findAllByOrderByIdAsc();
		return procurementType.stream().map(procurementTypeMapper::mapToProcurementTypeResponse).toList();
	}

	@Override
	public List<ProcurementType> findAll() {
		return procurementTypeRepo.findAllByOrderByIdAsc();
	}

	@Override
	public ProcurementTypeResponse updateProcurementType(Long id, ProcurementTypeRequest procurementTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(procurementTypeRequest);
		String existName = procurementTypeRequest.getProcurementTypeName();
		String existCode = procurementTypeRequest.getProcurementTypeCode();
		boolean exists = procurementTypeRepo.existsByProcurementTypeCodeAndProcurementTypeNameAndIdNot(existCode,
				existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			ProcurementType existingProcurementType = this.findProcurementTypeById(id);
			if (!existingProcurementType.getProcurementTypeCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "ProcurementType Code",
						existingProcurementType.getProcurementTypeCode(), existCode));
				existingProcurementType.setProcurementTypeCode(existCode);
			}
			if (!existingProcurementType.getProcurementTypeName().equals(existName)) {
				auditFields.add(new AuditFields(null, "ProcurementType Name",
						existingProcurementType.getProcurementTypeName(), existName));
				existingProcurementType.setProcurementTypeName(existName);
			}
			if (!existingProcurementType.getProcurementTypeStatus()
					.equals(procurementTypeRequest.getProcurementTypeStatus())) {
				auditFields.add(new AuditFields(null, "ProcurementType Status",
						existingProcurementType.getProcurementTypeStatus(),
						procurementTypeRequest.getProcurementTypeStatus()));
				existingProcurementType.setProcurementTypeStatus(procurementTypeRequest.getProcurementTypeStatus());
			}
			if (!existingProcurementType.getDynamicFields().equals(procurementTypeRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : procurementTypeRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingProcurementType.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingProcurementType.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingProcurementType.updateAuditHistory(auditFields);
			procurementTypeRepo.save(existingProcurementType);
			return procurementTypeMapper.mapToProcurementTypeResponse(existingProcurementType);
		} else {
			throw new AlreadyExistsException("ProcurementType with this name already exists");
		}
	}

	@Override
	public List<ProcurementTypeResponse> updateBulkStatusProcurementTypeId(List<Long> id)
			throws ResourceNotFoundException {
		List<ProcurementType> existingProcurementTypes = this.findAllProcTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingProcurementTypes.forEach(existingProcurementType -> {
			if (existingProcurementType.getProcurementTypeStatus() != null) {
				auditFields.add(new AuditFields(null, "ProcurementType Status",
						existingProcurementType.getProcurementTypeStatus(),
						!existingProcurementType.getProcurementTypeStatus()));
				existingProcurementType.setProcurementTypeStatus(!existingProcurementType.getProcurementTypeStatus());
			}
			existingProcurementType.updateAuditHistory(auditFields);

		});
		procurementTypeRepo.saveAll(existingProcurementTypes);
		return existingProcurementTypes.stream().map(procurementTypeMapper::mapToProcurementTypeResponse).toList();
	}

	@Override
	public ProcurementTypeResponse updateStatusUsingProcurementTypeId(Long id) throws ResourceNotFoundException {
		ProcurementType existingProcurementType = this.findProcurementTypeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingProcurementType.getProcurementTypeStatus() != null) {
			auditFields.add(
					new AuditFields(null, "ProcurementType Status", existingProcurementType.getProcurementTypeStatus(),
							!existingProcurementType.getProcurementTypeStatus()));
			existingProcurementType.setProcurementTypeStatus(!existingProcurementType.getProcurementTypeStatus());
		}
		existingProcurementType.updateAuditHistory(auditFields);
		procurementTypeRepo.save(existingProcurementType);
		return procurementTypeMapper.mapToProcurementTypeResponse(existingProcurementType);
	}

	@Override
	public void deleteProcurementType(Long id) throws ResourceNotFoundException {
		ProcurementType procurementType = this.findProcurementTypeById(id);
		procurementTypeRepo.deleteById(procurementType.getId());
	}

	@Override
	public void deleteBatchProcurementType(List<Long> ids) throws ResourceNotFoundException {
		List<ProcurementType> procurementTypes = this.findAllProcTypeById(ids);
		if (!procurementTypes.isEmpty()) {
			procurementTypeRepo.deleteAll(procurementTypes);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ProcurementType";
		Class<?> clazz = ProcurementTypeRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ProcurementType_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<ProcurementType> dataS = excelFileHelper.readDataFromExcel(file.getInputStream(), ProcurementType.class);
		for (ProcurementType data : dataS) {
			if (!procurementTypeRepo.existsByProcurementTypeCodeAndProcurementTypeName(data.getProcurementTypeCode(),
					data.getProcurementTypeName())) {

				this.procurementTypeRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "ProcurementType";
		Class<?> clazz = ProcurementTypeResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ProcurementType_";
		List<ProcurementTypeResponse> allValue = getAllProcurementType();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertProcurementTypeListToMap(List<ProcurementType> procurementTypeList) {
		List<Map<String, Object>> procurementType = new ArrayList<>();

		for (ProcurementType procurementTypeS : procurementTypeList) {
			Map<String, Object> procurementTypeData = new HashMap<>();
			procurementTypeData.put("Id", procurementTypeS.getId());
			procurementTypeData.put("Name", procurementTypeS.getProcurementTypeName());
			procurementTypeData.put("Status", procurementTypeS.getProcurementTypeStatus());
			procurementType.add(procurementTypeData);
		}
		return procurementType;
	}

	private void validateDynamicFields(ProcurementType procurementType) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : procurementType.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = ProcurementType.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private ProcurementType findProcurementTypeById(Long id) throws ResourceNotFoundException {
		return procurementTypeRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Procurement Type with ID " + id + " not found."));
	}

	private List<ProcurementType> findAllProcTypeById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<ProcurementType> types = procurementTypeRepo.findAllById(ids);
		List<ProcurementType> foundTypes = types.stream().filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Procurement Type with IDs " + missingIds + " not found.");
		}
		return foundTypes;
	}

}
