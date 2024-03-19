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
import com.example.mrpdataservice.entity.ScheduleMargin;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.mapping.ScheduleMarginMapper;
import com.example.mrpdataservice.repository.ScheduleMarginRepo;
import com.example.mrpdataservice.request.ScheduleMarginRequest;
import com.example.mrpdataservice.response.ScheduleMarginResponse;
import com.example.mrpdataservice.service.ScheduleMarginService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleMarginServiceImpl implements ScheduleMarginService {

	private final ScheduleMarginRepo scheduleMarginRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ScheduleMarginMapper scheduleMarginMapper;
	private final DynamicClient dynamicClient;

	@Override
	public ScheduleMarginResponse saveScheduleMargin(ScheduleMarginRequest scheduleMarginRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(scheduleMarginRequest);
		boolean exists = scheduleMarginRepo.existsByScheduleMarginCodeAndScheduleMarginName(
				scheduleMarginRequest.getScheduleMarginCode(), scheduleMarginRequest.getScheduleMarginName());
		if (!exists) {
			ScheduleMargin scheduleMargin = scheduleMarginMapper.mapToScheduleMargin(scheduleMarginRequest);
			validateDynamicFields(scheduleMargin);
			scheduleMarginRepo.save(scheduleMargin);
			return scheduleMarginMapper.mapToScheduleMarginResponse(scheduleMargin);
		} else {
			throw new AlreadyExistsException("ScheduleMargin with this name already exists");
		}
	}

	@Override
	public ScheduleMarginResponse getScheduleMarginById(Long id) throws ResourceNotFoundException {
		ScheduleMargin scheduleMargin = this.findScheduleMarginById(id);
		return scheduleMarginMapper.mapToScheduleMarginResponse(scheduleMargin);
	}

	@Override
	public List<ScheduleMarginResponse> getAllScheduleMargin() {
		return scheduleMarginRepo.findAllByOrderByIdAsc().stream()
				.map(scheduleMarginMapper::mapToScheduleMarginResponse).toList();
	}

	@Override
	public List<ScheduleMargin> findAll() {
		return scheduleMarginRepo.findAllByOrderByIdAsc();
	}

	@Override
	public ScheduleMarginResponse updateScheduleMargin(Long id, ScheduleMarginRequest scheduleMarginRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(scheduleMarginRequest);

		String existName = scheduleMarginRequest.getScheduleMarginName();
		String existCode = scheduleMarginRequest.getScheduleMarginCode();
		boolean exists = scheduleMarginRepo.existsByScheduleMarginCodeAndScheduleMarginNameAndIdNot(existCode,
				existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			ScheduleMargin existingScheduleMargin = this.findScheduleMarginById(id);
			if (!existingScheduleMargin.getScheduleMarginCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "ScheduleMargin Code",
						existingScheduleMargin.getScheduleMarginCode(), existCode));
				existingScheduleMargin.setScheduleMarginCode(existCode);
			}
			if (!existingScheduleMargin.getScheduleMarginName().equals(existName)) {
				auditFields.add(new AuditFields(null, "ScheduleMargin Name",
						existingScheduleMargin.getScheduleMarginName(), existName));
				existingScheduleMargin.setScheduleMarginName(existName);
			}
			if (!existingScheduleMargin.getScheduleMarginStatus()
					.equals(scheduleMarginRequest.getScheduleMarginStatus())) {
				auditFields.add(
						new AuditFields(null, "ScheduleMargin Status", existingScheduleMargin.getScheduleMarginStatus(),
								scheduleMarginRequest.getScheduleMarginStatus()));
				existingScheduleMargin.setScheduleMarginStatus(scheduleMarginRequest.getScheduleMarginStatus());
			}
			if (!existingScheduleMargin.getDynamicFields().equals(scheduleMarginRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : scheduleMarginRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingScheduleMargin.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingScheduleMargin.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingScheduleMargin.updateAuditHistory(auditFields);
			scheduleMarginRepo.save(existingScheduleMargin);
			return scheduleMarginMapper.mapToScheduleMarginResponse(existingScheduleMargin);
		} else {
			throw new AlreadyExistsException("ScheduleMargin with this name already exists");
		}
	}

	@Override
	public List<ScheduleMarginResponse> updateBulkStatusScheduleMarginId(List<Long> id)
			throws ResourceNotFoundException {
		List<ScheduleMargin> existingScheduleMargins = this.findAllScheMargById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingScheduleMargins.forEach(existingScheduleMargin -> {
			if (existingScheduleMargin.getScheduleMarginStatus() != null) {
				auditFields.add(
						new AuditFields(null, "ScheduleMargin Status", existingScheduleMargin.getScheduleMarginStatus(),
								!existingScheduleMargin.getScheduleMarginStatus()));
				existingScheduleMargin.setScheduleMarginStatus(!existingScheduleMargin.getScheduleMarginStatus());
			}
			existingScheduleMargin.updateAuditHistory(auditFields);

		});
		scheduleMarginRepo.saveAll(existingScheduleMargins);
		return existingScheduleMargins.stream().map(scheduleMarginMapper::mapToScheduleMarginResponse).toList();
	}

	@Override
	public ScheduleMarginResponse updateStatusUsingScheduleMarginId(Long id) throws ResourceNotFoundException {
		ScheduleMargin existingScheduleMargin = this.findScheduleMarginById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingScheduleMargin.getScheduleMarginStatus() != null) {
			auditFields.add(
					new AuditFields(null, "ScheduleMargin Status", existingScheduleMargin.getScheduleMarginStatus(),
							!existingScheduleMargin.getScheduleMarginStatus()));
			existingScheduleMargin.setScheduleMarginStatus(!existingScheduleMargin.getScheduleMarginStatus());
		}
		existingScheduleMargin.updateAuditHistory(auditFields);
		scheduleMarginRepo.save(existingScheduleMargin);
		return scheduleMarginMapper.mapToScheduleMarginResponse(existingScheduleMargin);
	}

	@Override
	public void deleteScheduleMargin(Long id) throws ResourceNotFoundException {
		ScheduleMargin scheduleMargin = this.findScheduleMarginById(id);
		if (scheduleMargin != null) {
			scheduleMarginRepo.delete(scheduleMargin);
		}
	}

	@Override
	public void deleteBatchScheduleMargin(List<Long> ids) throws ResourceNotFoundException {
		List<ScheduleMargin> scheduleMargins = this.findAllScheMargById(ids);
		if (!scheduleMargins.isEmpty()) {
			scheduleMarginRepo.deleteAll(scheduleMargins);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ScheduleMargin";
		Class<?> clazz = ScheduleMarginRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ScheduleMargin_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<ScheduleMargin> data = excelFileHelper.readDataFromExcel(file.getInputStream(), ScheduleMargin.class);
		for (ScheduleMargin dataS : data) {
			if (!scheduleMarginRepo.existsByScheduleMarginCodeAndScheduleMarginName(dataS.getScheduleMarginCode(),
					dataS.getScheduleMarginName())) {

				this.scheduleMarginRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "ScheduleMargin";
		Class<?> clazz = ScheduleMarginResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ScheduleMargin_";
		List<ScheduleMarginResponse> allValue = getAllScheduleMargin();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertScheduleMarginListToMap(List<ScheduleMargin> scheduleMarginList) {
		List<Map<String, Object>> scheduleMargin = new ArrayList<>();

		for (ScheduleMargin scheduleMarginS : scheduleMarginList) {
			Map<String, Object> scheduleMarginSData = new HashMap<>();
			scheduleMarginSData.put("Id", scheduleMarginS.getId());
			scheduleMarginSData.put("Name", scheduleMarginS.getScheduleMarginName());
			scheduleMarginSData.put("Status", scheduleMarginS.getScheduleMarginStatus());
			scheduleMargin.add(scheduleMarginSData);
		}
		return scheduleMargin;
	}

	private void validateDynamicFields(ScheduleMargin scheduleMargin) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : scheduleMargin.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = ScheduleMargin.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private ScheduleMargin findScheduleMarginById(Long id) throws ResourceNotFoundException {
		return scheduleMarginRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("ScheduleMargin with ID " + id + " not found"));
	}

	private List<ScheduleMargin> findAllScheMargById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<ScheduleMargin> margins = scheduleMarginRepo.findAllById(ids);
		List<ScheduleMargin> foundMargins = margins.stream().filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Schedule Margin with IDs " + missingIds + " not found.");
		}
		return foundMargins;
	}

}
