package com.example.mrpdataservice.serviceimpl;

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

import com.example.mrpdataservice.client.Dynamic.DynamicClient;
import com.example.mrpdataservice.entity.AuditFields;
import com.example.mrpdataservice.entity.ProcurementType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
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
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public ProcurementTypeResponse saveProcurementType(ProcurementTypeRequest procurementTypeRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(procurementTypeRequest);
		boolean exists = procurementTypeRepo.existsByProcurementTypeCodeAndProcurementTypeName(
				procurementTypeRequest.getProcurementTypeCode(), procurementTypeRequest.getProcurementTypeName());
		if (!exists) {
			ProcurementType procurementType = modelMapper.map(procurementTypeRequest, ProcurementType.class);
			for (Map.Entry<String, Object> entryField : procurementType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ProcurementType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			procurementTypeRepo.save(procurementType);
			return mapToProcurementTypeResponse(procurementType);
		} else {
			throw new AlreadyExistsException("ProcurementType with this name already exists");
		}
	}

	@Override
	public ProcurementTypeResponse getProcurementTypeById(Long id) throws ResourceNotFoundException {
		ProcurementType procurementType = this.findProcurementTypeById(id);
		return mapToProcurementTypeResponse(procurementType);
	}

	@Override
	public List<ProcurementTypeResponse> getAllProcurementType() {
		List<ProcurementType> procurementType = procurementTypeRepo.findAllByOrderByIdAsc();
		return procurementType.stream().map(this::mapToProcurementTypeResponse).toList();
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
			return mapToProcurementTypeResponse(existingProcurementType);
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
		return existingProcurementTypes.stream().map(this::mapToProcurementTypeResponse).toList();
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
		return mapToProcurementTypeResponse(existingProcurementType);
	}

	@Override
	public void deleteProcurementType(Long id) throws ResourceNotFoundException {
		ProcurementType procurementType = this.findProcurementTypeById(id);
		procurementTypeRepo.deleteById(procurementType.getId());
	}

	@Override
	public void deleteBatchProcurementType(List<Long> ids) throws ResourceNotFoundException {
		this.findAllProcTypeById(ids);
		procurementTypeRepo.deleteAllByIdInBatch(ids);
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

	private ProcurementTypeResponse mapToProcurementTypeResponse(ProcurementType procurementType) {
		return modelMapper.map(procurementType, ProcurementTypeResponse.class);
	}

	private ProcurementType findProcurementTypeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ProcurementType> procurementType = procurementTypeRepo.findById(id);
		if (procurementType.isEmpty()) {
			throw new ResourceNotFoundException("Procurement Type with ID " + id + " not found.");
		}
		return procurementType.get();
	}

	private List<ProcurementType> findAllProcTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ProcurementType> types = procurementTypeRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> types.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Procurement Type with IDs " + missingIds + " not found.");
		}
		return types;
	}
}
