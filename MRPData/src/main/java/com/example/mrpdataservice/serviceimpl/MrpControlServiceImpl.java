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

import com.example.mrpdataservice.client.MrpPlantResponse;
import com.example.mrpdataservice.client.Dynamic.DynamicClient;
import com.example.mrpdataservice.client.Plant.MrpPlantClient;
import com.example.mrpdataservice.entity.AuditFields;
import com.example.mrpdataservice.entity.MrpControl;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.repository.MrpControlRepo;
import com.example.mrpdataservice.request.MrpControlRequest;
import com.example.mrpdataservice.response.MrpControlResponse;
import com.example.mrpdataservice.service.MrpControlService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MrpControlServiceImpl implements MrpControlService {

	private final MrpControlRepo mrpControlRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ModelMapper modelMapper;
	private final MrpPlantClient mrpPlantClient;
	private final DynamicClient dynamicClient;

	@Override
	public MrpControlResponse saveMrpControl(MrpControlRequest mrpControlRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(mrpControlRequest);
		boolean exists = mrpControlRepo.existsByMrpControlCodeAndMrpControlName(mrpControlRequest.getMrpControlCode(),
				mrpControlRequest.getMrpControlName());
		if (!exists) {
			MrpControl mrpControl = modelMapper.map(mrpControlRequest, MrpControl.class);
			for (Map.Entry<String, Object> entryField : mrpControl.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MrpControl.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			mrpControl.setId(null);
			mrpControlRepo.save(mrpControl);
			return mapToMrpControlResponse1(mrpControl);
		} else {
			throw new AlreadyExistsException("MrpControl with this name already exists");
		}
	}

	@Override
	public MrpControlResponse getMrpControlById(Long id) throws ResourceNotFoundException {
		MrpControl mrpControl = this.findMrpControlById(id);
		return mapToMrpControlResponse(mrpControl);
	}

	@Override
	public MrpControlResponse getMrpControlByName(String name) throws ResourceNotFoundException {
		MrpControl mrpControl = this.findMrpControlByName(name);
		return mapToMrpControlResponse(mrpControl);
	}

	@Override
	public List<MrpControlResponse> getAllMrpControl() {
		List<MrpControl> mrpControl = mrpControlRepo.findAllByOrderByIdAsc();
		return mrpControl.stream().map(this::mapToMrpControlResponse).toList();
	}

	@Override
	public List<MrpPlantResponse> getAllMrpControlByPlant() throws ResourceNotFoundException {
		List<MrpControl> mrpControls = mrpControlRepo.findAll();
		List<MrpPlantResponse> responseList = new ArrayList<>();
		for (MrpControl mrpControl : mrpControls) {
			MrpPlantResponse mrpPlantResponse = mapToMrpPlantResponse(mrpControl);
			responseList.add(mrpPlantResponse);
		}
		return responseList;
	}

	@Override
	public List<MrpControl> findAll() {
		return mrpControlRepo.findAllByOrderByIdAsc();
	}

	@Override
	public MrpControlResponse updateMrpControl(Long id, MrpControlRequest mrpControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(mrpControlRequest);
		String existName = mrpControlRequest.getMrpControlName();
		String existCode = mrpControlRequest.getMrpControlCode();
		boolean exists = mrpControlRepo.existsByMrpControlCodeAndMrpControlNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			MrpControl existingMrpControl = this.findMrpControlById(id);
			if (!existingMrpControl.getMrpControlCode().equals(existCode)) {
				auditFields.add(
						new AuditFields(null, "MrpControl Code", existingMrpControl.getMrpControlCode(), existCode));
				existingMrpControl.setMrpControlCode(existCode);
			}
			if (!existingMrpControl.getMrpControlName().equals(existName)) {
				auditFields.add(
						new AuditFields(null, "MrpControl Name", existingMrpControl.getMrpControlName(), existName));
				existingMrpControl.setMrpControlName(existName);
			}
			if (!existingMrpControl.getMrpControlStatus().equals(mrpControlRequest.getMrpControlStatus())) {
				auditFields.add(new AuditFields(null, "MrpControl Status", existingMrpControl.getMrpControlStatus(),
						mrpControlRequest.getMrpControlStatus()));
				existingMrpControl.setMrpControlStatus(mrpControlRequest.getMrpControlStatus());
			}
			if (!existingMrpControl.getPlantId().equals(mrpControlRequest.getPlantId())) {
				auditFields.add(new AuditFields(null, "Plant", existingMrpControl.getPlantId(),
						mrpControlRequest.getPlantId()));
				existingMrpControl.setPlantId(mrpControlRequest.getPlantId());
			}
			if (!existingMrpControl.getDynamicFields().equals(mrpControlRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : mrpControlRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingMrpControl.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingMrpControl.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingMrpControl.updateAuditHistory(auditFields);
			MrpControl mrp = mrpControlRepo.save(existingMrpControl);
			return mapToMrpControlResponse(mrp);
		} else {
			throw new AlreadyExistsException("MrpControl with this name already exists");
		}
	}

	@Override
	public List<MrpControlResponse> updateBulkStatusMrpControlId(List<Long> id) throws ResourceNotFoundException {
		List<MrpControl> existingMrpControls = this.findAllMrpControlById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingMrpControls.forEach(existingMrpControl -> {
			if (existingMrpControl.getMrpControlStatus() != null) {
				auditFields.add(new AuditFields(null, "MrpControl Status", existingMrpControl.getMrpControlStatus(),
						!existingMrpControl.getMrpControlStatus()));
				existingMrpControl.setMrpControlStatus(!existingMrpControl.getMrpControlStatus());
			}
			existingMrpControl.updateAuditHistory(auditFields);
		});
		mrpControlRepo.saveAll(existingMrpControls);
		return existingMrpControls.stream().map(this::mapToMrpControlResponse).toList();
	}

	@Override
	public MrpControlResponse updateStatusUsingMrpControlId(Long id) throws ResourceNotFoundException {
		MrpControl existingMrpControl = this.findMrpControlById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingMrpControl.getMrpControlStatus() != null) {
			auditFields.add(new AuditFields(null, "MrpControl Status", existingMrpControl.getMrpControlStatus(),
					!existingMrpControl.getMrpControlStatus()));
			existingMrpControl.setMrpControlStatus(!existingMrpControl.getMrpControlStatus());
		}
		existingMrpControl.updateAuditHistory(auditFields);
		mrpControlRepo.save(existingMrpControl);
		return mapToMrpControlResponse(existingMrpControl);
	}

	@Override
	public void deleteMrpControl(Long id) throws ResourceNotFoundException {
		MrpControl mrpControl = this.findMrpControlById(id);
		mrpControlRepo.deleteById(mrpControl.getId());
	}

	@Override
	public void deleteBatchMrpControl(List<Long> ids) throws ResourceNotFoundException {
		this.findAllMrpControlById(ids);
		mrpControlRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "MrpControl";
		Class<?> clazz = MrpControlRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "MrpControl_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<MrpControl> dataS = excelFileHelper.readDataFromExcel(file.getInputStream(), MrpControl.class);
		for (MrpControl data : dataS) {
			if (!mrpControlRepo.existsByMrpControlCodeAndMrpControlName(data.getMrpControlCode(),
					data.getMrpControlName())) {

				this.mrpControlRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "MrpControl";
		Class<?> clazz = MrpControlResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "MrpControl_";
		List<MrpControlResponse> allValue = getAllMrpControl();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertMrpControlListToMap(List<MrpControl> mrpControlsList) {
		List<Map<String, Object>> mrpControl = new ArrayList<>();

		for (MrpControl mrpControls : mrpControlsList) {
			Map<String, Object> mrpControlData = new HashMap<>();
			mrpControlData.put("Id", mrpControls.getId());
			mrpControlData.put("Name", mrpControls.getMrpControlName());
			mrpControlData.put("Status", mrpControls.getMrpControlStatus());
			mrpControl.add(mrpControlData);
		}
		return mrpControl;
	}

	private MrpControlResponse mapToMrpControlResponse(MrpControl mrpControl) {
		return modelMapper.map(mrpControl, MrpControlResponse.class);
	}

	private MrpControlResponse mapToMrpControlResponse1(MrpControl mrpControl) {
		return modelMapper.map(mrpControl, MrpControlResponse.class);
	}

	private MrpControl findMrpControlById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<MrpControl> mrpControl = mrpControlRepo.findById(id);
		if (mrpControl.isEmpty()) {
			throw new ResourceNotFoundException("Mrp Control with ID " + id + " not found.");
		}
		return mrpControl.get();
	}

	private MrpControl findMrpControlByName(String name) throws ResourceNotFoundException {
		Optional<MrpControl> mrpControl = mrpControlRepo.findByMrpControlName(name);
		if (mrpControl.isEmpty()) {
			throw new ResourceNotFoundException("Mrp Control with Name " + name + " not found.");
		}
		return mrpControl.get();
	}

	private List<MrpControl> findAllMrpControlById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<MrpControl> controls = mrpControlRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> controls.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Mrp Control with IDs " + missingIds + " not found.");
		}
		return controls;
	}

	private MrpPlantResponse mapToMrpPlantResponse(MrpControl mrpControl) throws ResourceNotFoundException {
		MrpPlantResponse mrpPlantResponse = modelMapper.map(mrpControl, MrpPlantResponse.class);
		if (mrpPlantClient == null) {
			throw new IllegalStateException("Plant Service is not initiated");
		}
		mrpPlantResponse.setPlant(mrpPlantClient.getPlantById(mrpControl.getPlantId()));
		return mrpPlantResponse;
	}

}
