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
import com.example.mrpdataservice.entity.AvailCheck;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
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
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String AVAIL_CHECK_NOT_FOUND_MESSAGE = null;

	@Override
	public AvailCheckResponse saveAvailCheck(AvailCheckRequest availCheckRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = availCheckRepo.existsByAvailCheckCodeAndAvailCheckName(availCheckRequest.getAvailCheckCode(),
				availCheckRequest.getAvailCheckName());
		if (!exists) {
			AvailCheck availCheck = modelMapper.map(availCheckRequest, AvailCheck.class);
			for (Map.Entry<String, Object> entryField : availCheck.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AvailCheck.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			availCheckRepo.save(availCheck);
			return mapToAvailCheckResponse(availCheck);
		}

		else {
			throw new AlreadyExistsException("AvailCheck with this name already exists");
		}
	}

	@Override
	public AvailCheckResponse getAvailCheckById(Long id) throws ResourceNotFoundException {
		AvailCheck availCheck = this.findAvailCheckById(id);
		return mapToAvailCheckResponse(availCheck);
	}

	@Override
	public List<AvailCheckResponse> getAllAvailCheck() {
		List<AvailCheck> availCheck = availCheckRepo.findAllByOrderByIdAsc();
		return availCheck.stream().map(this::mapToAvailCheckResponse).toList();
	}

	@Override
	public List<AvailCheck> findAll() {
		return availCheckRepo.findAllByOrderByIdAsc();
	}

	@Override
	public AvailCheckResponse updateAvailCheck(Long id, AvailCheckRequest availCheckRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String exist = availCheckRequest.getAvailCheckName();
		String existCode = availCheckRequest.getAvailCheckCode();
		boolean exists = availCheckRepo.existsByAvailCheckCodeAndAvailCheckNameAndIdNot(existCode, exist, id);
		if (!exists) {
			AvailCheck existingAvailCheck = this.findAvailCheckById(id);
			modelMapper.map(availCheckRequest, existingAvailCheck);
			for (Map.Entry<String, Object> entryField : existingAvailCheck.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AvailCheck.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			availCheckRepo.save(existingAvailCheck);
			return mapToAvailCheckResponse(existingAvailCheck);
		} else {
			throw new AlreadyExistsException("AvailCheck with this name already exists");
		}
	}

	@Override
	public List<AvailCheckResponse> updateBulkStatusAvailCheckId(List<Long> id) throws ResourceNotFoundException {
		List<AvailCheck> existingAvailCheck = this.findAllAvailCheckById(id);
		for (AvailCheck availCheck : existingAvailCheck) {
			availCheck.setAvailCheckStatus(!availCheck.getAvailCheckStatus());
		}
		availCheckRepo.saveAll(existingAvailCheck);
		return existingAvailCheck.stream().map(this::mapToAvailCheckResponse).toList();
	}

	@Override
	public AvailCheckResponse updateStatusUsingAvailCheckId(Long id) throws ResourceNotFoundException {
		AvailCheck existingAvailCheck = this.findAvailCheckById(id);
		existingAvailCheck.setAvailCheckStatus(!existingAvailCheck.getAvailCheckStatus());
		availCheckRepo.save(existingAvailCheck);
		return mapToAvailCheckResponse(existingAvailCheck);
	}

	@Override
	public void deleteAvailCheck(Long id) throws ResourceNotFoundException {
		AvailCheck availCheck = this.findAvailCheckById(id);
		availCheckRepo.deleteById(availCheck.getId());
	}

	@Override
	public void deleteBatchAvailCheck(List<Long> ids) throws ResourceNotFoundException {
		this.findAllAvailCheckById(ids);
		availCheckRepo.deleteAllByIdInBatch(ids);
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

	private AvailCheckResponse mapToAvailCheckResponse(AvailCheck availCheck) {
		return modelMapper.map(availCheck, AvailCheckResponse.class);
	}

	private AvailCheck findAvailCheckById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<AvailCheck> availCheck = availCheckRepo.findById(id);
		if (availCheck.isEmpty()) {
			throw new ResourceNotFoundException(AVAIL_CHECK_NOT_FOUND_MESSAGE);
		}
		return availCheck.get();
	}

	private List<AvailCheck> findAllAvailCheckById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<AvailCheck> availChecks = availCheckRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> availChecks.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Avail Check with IDs " + missingIds + " not found.");
		}
		return availChecks;
	}

}
