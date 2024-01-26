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
import com.example.mrpdataservice.entity.MrpType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.repository.MrpTypeRepo;
import com.example.mrpdataservice.request.MrpTypeRequest;
import com.example.mrpdataservice.response.MrpTypeResponse;
import com.example.mrpdataservice.service.MrpTypeService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MrpTypeServiceImpl implements MrpTypeService {

	private final MrpTypeRepo mrpTypeRepo;

	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String MRP_TYPE_NOT_FOUND_MESSAGE = null;
	private final ExcelFileHelper excelFileHelper;

	@Override
	public MrpTypeResponse saveMrpType(MrpTypeRequest mrpTypeRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = mrpTypeRepo.existsByMrpTypeCodeAndMrpTypeName(mrpTypeRequest.getMrpTypeCode(),
				mrpTypeRequest.getMrpTypeName());
		if (!exists) {
			MrpType mrpType = modelMapper.map(mrpTypeRequest, MrpType.class);
			for (Map.Entry<String, Object> entryField : mrpType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MrpType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			mrpTypeRepo.save(mrpType);
			return mapToMrpTypeResponse(mrpType);
		} else {
			throw new AlreadyExistsException("MrpType with this name already exists");
		}
	}

	@Override
	public MrpTypeResponse getMrpTypeById(Long id) throws ResourceNotFoundException {
		MrpType valuationCategory = this.findMrpTypeById(id);
		return mapToMrpTypeResponse(valuationCategory);
	}

	@Override
	public List<MrpTypeResponse> getAllMrpType() {
		List<MrpType> valuationCategory = mrpTypeRepo.findAllByOrderByIdAsc();
		return valuationCategory.stream().map(this::mapToMrpTypeResponse).toList();
	}

	@Override
	public List<MrpType> findAll() {
		return mrpTypeRepo.findAllByOrderByIdAsc();
	}

	@Override
	public MrpTypeResponse updateMrpType(Long id, MrpTypeRequest mrpTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String exist = mrpTypeRequest.getMrpTypeName();
		String existCode = mrpTypeRequest.getMrpTypeCode();
		boolean exists = mrpTypeRepo.existsByMrpTypeCodeAndMrpTypeNameAndIdNot(existCode, exist, id);
		if (!exists) {
			MrpType existingMrpType = this.findMrpTypeById(id);
			modelMapper.map(mrpTypeRequest, existingMrpType);
			for (Map.Entry<String, Object> entryField : existingMrpType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MrpType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			mrpTypeRepo.save(existingMrpType);
			return mapToMrpTypeResponse(existingMrpType);
		} else {
			throw new AlreadyExistsException("MrpType with this name already exists");
		}
	}

	@Override
	public List<MrpTypeResponse> updateBulkStatusMrpTypeId(List<Long> id) throws ResourceNotFoundException {
		List<MrpType> existingMrpType = this.findAllMrpTypeById(id);
		for (MrpType valuationCategory : existingMrpType) {
			valuationCategory.setMrpTypeStatus(!valuationCategory.getMrpTypeStatus());
		}
		mrpTypeRepo.saveAll(existingMrpType);
		return existingMrpType.stream().map(this::mapToMrpTypeResponse).toList();
	}

	@Override
	public MrpTypeResponse updateStatusUsingMrpTypeId(Long id) throws ResourceNotFoundException {
		MrpType existingMrpType = this.findMrpTypeById(id);
		existingMrpType.setMrpTypeStatus(!existingMrpType.getMrpTypeStatus());
		mrpTypeRepo.save(existingMrpType);
		return mapToMrpTypeResponse(existingMrpType);
	}

	@Override
	public void deleteMrpType(Long id) throws ResourceNotFoundException {
		MrpType valuationCategory = this.findMrpTypeById(id);
		mrpTypeRepo.deleteById(valuationCategory.getId());
	}

	@Override
	public void deleteBatchMrpType(List<Long> ids) throws ResourceNotFoundException {
		this.findAllMrpTypeById(ids);
		mrpTypeRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "MrpType";
		Class<?> clazz = MrpTypeRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "MrpType_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<MrpType> dataS = excelFileHelper.readDataFromExcel(file.getInputStream(), MrpType.class);
		for (MrpType data : dataS) {
			if (!mrpTypeRepo.existsByMrpTypeCodeAndMrpTypeName(data.getMrpTypeCode(), data.getMrpTypeName())) {

				this.mrpTypeRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "MrpType";
		Class<?> clazz = MrpTypeResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Department_";
		List<MrpTypeResponse> allValue = getAllMrpType();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertMrpTypeListToMap(List<MrpType> mrpTypeList) {
		List<Map<String, Object>> mrpType = new ArrayList<>();

		for (MrpType mrpTypes : mrpTypeList) {
			Map<String, Object> mrpTypeData = new HashMap<>();
			mrpTypeData.put("Id", mrpTypes.getId());
			mrpTypeData.put("Name", mrpTypes.getMrpTypeName());
			mrpTypeData.put("Status", mrpTypes.getMrpTypeStatus());
			mrpType.add(mrpTypeData);
		}
		return mrpType;
	}

	private MrpTypeResponse mapToMrpTypeResponse(MrpType valuationCategory) {
		return modelMapper.map(valuationCategory, MrpTypeResponse.class);
	}

	private MrpType findMrpTypeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<MrpType> valuationCategory = mrpTypeRepo.findById(id);
		if (valuationCategory.isEmpty()) {
			throw new ResourceNotFoundException(MRP_TYPE_NOT_FOUND_MESSAGE);
		}
		return valuationCategory.get();
	}

	private List<MrpType> findAllMrpTypeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<MrpType> mrpTypes = mrpTypeRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> mrpTypes.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Mrp Type with IDs " + missingIds + " not found.");
		}
		return mrpTypes;
	}
}
