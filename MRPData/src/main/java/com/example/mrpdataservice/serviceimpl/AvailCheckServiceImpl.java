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
import com.example.mrpdataservice.entity.AvailCheck;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.mapping.AvailCheckMapper;
import com.example.mrpdataservice.repository.AvailCheckRepo;
import com.example.mrpdataservice.request.AvailCheckRequest;
import com.example.mrpdataservice.response.AvailCheckResponse;
import com.example.mrpdataservice.service.AvailCheckService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailCheckServiceImpl implements AvailCheckService {
	private final ExcelFileHelper excelFileHelper;
	private final AvailCheckRepo availCheckRepo;
	private final AvailCheckMapper availCheckMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AvailCheckResponse saveAvailCheck(AvailCheckRequest availCheckRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(availCheckRequest);
		String availCheckCode = availCheckRequest.getAvailCheckCode();
		String availCheckName = availCheckRequest.getAvailCheckName();
		if (availCheckRepo.existsByAvailCheckCodeAndAvailCheckName(availCheckCode, availCheckName)) {
			throw new AlreadyExistsException("AvailCheck with this name already exists");
		}
		AvailCheck availCheck = availCheckMapper.mapToAvailCheck(availCheckRequest);
		validateDynamicFields(availCheck);
		availCheckRepo.save(availCheck);
		return availCheckMapper.mapToAvailCheckResponse(availCheck);

	}

	@Override
	public AvailCheckResponse getAvailCheckById(Long id) throws ResourceNotFoundException {
		AvailCheck availCheck = this.findAvailCheckById(id);
		return availCheckMapper.mapToAvailCheckResponse(availCheck);
	}

	@Override
	public List<AvailCheckResponse> getAllAvailCheck() {
		return availCheckRepo.findAllByOrderByIdAsc().stream().map(availCheckMapper::mapToAvailCheckResponse).toList();
	}

	@Override
	public List<AvailCheck> findAll() {
		return availCheckRepo.findAllByOrderByIdAsc();
	}

	@Override
	public AvailCheckResponse updateAvailCheck(Long id, AvailCheckRequest availCheckRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(availCheckRequest);
		String existName = availCheckRequest.getAvailCheckName();
		String existCode = availCheckRequest.getAvailCheckCode();
		boolean exists = availCheckRepo.existsByAvailCheckCodeAndAvailCheckNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			AvailCheck existingAvailCheck = this.findAvailCheckById(id);
			if (!existingAvailCheck.getAvailCheckCode().equals(existCode)) {
				auditFields.add(
						new AuditFields(null, "AvailCheck Code", existingAvailCheck.getAvailCheckCode(), existCode));
				existingAvailCheck.setAvailCheckCode(existCode);
			}
			if (!existingAvailCheck.getAvailCheckName().equals(existName)) {
				auditFields.add(
						new AuditFields(null, "AvailCheck Name", existingAvailCheck.getAvailCheckCode(), existName));
				existingAvailCheck.setAvailCheckName(existName);
			}
			if (!existingAvailCheck.getAvailCheckStatus().equals(availCheckRequest.getAvailCheckStatus())) {
				auditFields.add(new AuditFields(null, "AvailCheck Status", existingAvailCheck.getAvailCheckStatus(),
						availCheckRequest.getAvailCheckStatus()));
				existingAvailCheck.setAvailCheckStatus(availCheckRequest.getAvailCheckStatus());
			}
			if (!existingAvailCheck.getDynamicFields().equals(availCheckRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : availCheckRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingAvailCheck.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingAvailCheck.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingAvailCheck.updateAuditHistory(auditFields);
			availCheckRepo.save(existingAvailCheck);
			return availCheckMapper.mapToAvailCheckResponse(existingAvailCheck);
		} else {
			throw new AlreadyExistsException("AvailCheck with this name already exists");
		}
	}

	@Override
	public List<AvailCheckResponse> updateBulkStatusAvailCheckId(List<Long> id) throws ResourceNotFoundException {
		List<AvailCheck> existingAvailChecks = this.findAllAvailCheckById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingAvailChecks.forEach(existingAvailCheck -> {
			if (existingAvailCheck.getAvailCheckStatus() != null) {
				auditFields.add(new AuditFields(null, "AvailCheck Status", existingAvailCheck.getAvailCheckStatus(),
						!existingAvailCheck.getAvailCheckStatus()));
				existingAvailCheck.setAvailCheckStatus(!existingAvailCheck.getAvailCheckStatus());
			}
			existingAvailCheck.updateAuditHistory(auditFields);
		});
		availCheckRepo.saveAll(existingAvailChecks);
		return existingAvailChecks.stream().map(availCheckMapper::mapToAvailCheckResponse).toList();
	}

	@Override
	public AvailCheckResponse updateStatusUsingAvailCheckId(Long id) throws ResourceNotFoundException {
		AvailCheck existingAvailCheck = this.findAvailCheckById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingAvailCheck.getAvailCheckStatus() != null) {
			auditFields.add(new AuditFields(null, "AvailCheck Status", existingAvailCheck.getAvailCheckStatus(),
					!existingAvailCheck.getAvailCheckStatus()));
			existingAvailCheck.setAvailCheckStatus(!existingAvailCheck.getAvailCheckStatus());
		}
		existingAvailCheck.updateAuditHistory(auditFields);
		availCheckRepo.save(existingAvailCheck);
		return availCheckMapper.mapToAvailCheckResponse(existingAvailCheck);
	}

	@Override
	public void deleteAvailCheck(Long id) throws ResourceNotFoundException {
		AvailCheck availCheck = this.findAvailCheckById(id);
		if (availCheck != null) {
			availCheckRepo.delete(availCheck);
		}
	}

	@Override
	public void deleteBatchAvailCheck(List<Long> ids) throws ResourceNotFoundException {
		List<AvailCheck> availChecks = this.findAllAvailCheckById(ids);
		if (!availChecks.isEmpty()) {
			availCheckRepo.deleteAll(availChecks);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "AvailCheck";
		Class<?> clazz = AvailCheckRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "AvailCheck_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<AvailCheck> subChildGroup = excelFileHelper.readDataFromExcel(file.getInputStream(), AvailCheck.class);
		for (AvailCheck subChild : subChildGroup) {
			if (!availCheckRepo.existsByAvailCheckCodeAndAvailCheckName(subChild.getAvailCheckCode(),
					subChild.getAvailCheckName())) {

				this.availCheckRepo.save(subChild);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "AvailCheck";
		Class<?> clazz = AvailCheckResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "AvailCheck_";
		List<AvailCheckResponse> allValue = getAllAvailCheck();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertDepartmentListToMap(List<AvailCheck> availList) {
		List<Map<String, Object>> department = new ArrayList<>();

		for (AvailCheck avail : availList) {
			Map<String, Object> availData = new HashMap<>();
			availData.put("Id", avail.getId());
			availData.put("Name", avail.getAvailCheckName());
			availData.put("Status", avail.getAvailCheckStatus());
			department.add(availData);
		}
		return department;
	}

	private void validateDynamicFields(AvailCheck availCheck) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : availCheck.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = AvailCheck.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}

	}

	private AvailCheck findAvailCheckById(Long id) throws ResourceNotFoundException {
		return availCheckRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Avail Check with ID " + id + " not found"));
	}

	private List<AvailCheck> findAllAvailCheckById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);

		List<AvailCheck> availChecks = availCheckRepo.findAllById(ids);
		Set<Long> idSet = new HashSet<>(ids);

		List<AvailCheck> foundAvailChecks = availChecks.stream().filter(entity -> idSet.contains(entity.getId()))
				.toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Avail Check with IDs " + missingIds + " not found.");
		}

		return foundAvailChecks;
	}

}
