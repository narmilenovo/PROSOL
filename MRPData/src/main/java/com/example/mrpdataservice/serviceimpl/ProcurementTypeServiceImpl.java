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

	public static final String PROCUREMENT_TYPE_NOT_FOUND_MESSAGE = null;

	@Override
	public ProcurementTypeResponse saveProcurementType(ProcurementTypeRequest procurementTypeRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = procurementTypeRepo
				.existsByProcurementTypeCodeAndProcurementTypeName(procurementTypeRequest.getProcurementTypeCode(),
						procurementTypeRequest.getProcurementTypeName());
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
		String exist = procurementTypeRequest.getProcurementTypeName();
		String existCode = procurementTypeRequest.getProcurementTypeCode();
		boolean exists = procurementTypeRepo.existsByProcurementTypeCodeAndProcurementTypeNameAndIdNot(existCode, exist,
				id);
		if (!exists) {
			ProcurementType existingProcurementType = this.findProcurementTypeById(id);
			modelMapper.map(procurementTypeRequest, existingProcurementType);
			for (Map.Entry<String, Object> entryField : existingProcurementType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ProcurementType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			procurementTypeRepo.save(existingProcurementType);
			return mapToProcurementTypeResponse(existingProcurementType);
		} else {
			throw new AlreadyExistsException("ProcurementType with this name already exists");
		}
	}

	@Override
	public List<ProcurementTypeResponse> updateBulkStatusProcurementTypeId(List<Long> id)
			throws ResourceNotFoundException {
		List<ProcurementType> existingProcurementType = this.findAllProcTypeById(id);
		for (ProcurementType procurementType : existingProcurementType) {
			procurementType.setProcurementTypeStatus(!procurementType.getProcurementTypeStatus());
		}
		procurementTypeRepo.saveAll(existingProcurementType);
		return existingProcurementType.stream().map(this::mapToProcurementTypeResponse).toList();
	}

	@Override
	public ProcurementTypeResponse updateStatusUsingProcurementTypeId(Long id) throws ResourceNotFoundException {
		ProcurementType existingProcurementType = this.findProcurementTypeById(id);
		existingProcurementType.setProcurementTypeStatus(!existingProcurementType.getProcurementTypeStatus());
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
			throw new ResourceNotFoundException(PROCUREMENT_TYPE_NOT_FOUND_MESSAGE);
		}
		return procurementType.get();
	}

	private List<ProcurementType> findAllProcTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ProcurementType> types = procurementTypeRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> types.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Procurement Type with IDs " + missingIds + " not found.");
		}
		return types;
	}
}
