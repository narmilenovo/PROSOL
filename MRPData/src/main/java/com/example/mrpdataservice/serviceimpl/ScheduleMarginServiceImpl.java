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
import com.example.mrpdataservice.entity.ScheduleMargin;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
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
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String SCHEDULE_MARGIN_NOT_FOUND_MESSAGE = null;

	@Override
	public ScheduleMarginResponse saveScheduleMargin(ScheduleMarginRequest scheduleMarginRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = scheduleMarginRepo.existsByScheduleMarginCodeAndScheduleMarginName(
				scheduleMarginRequest.getScheduleMarginCode(), scheduleMarginRequest.getScheduleMarginName());
		if (!exists) {
			ScheduleMargin scheduleMargin = modelMapper.map(scheduleMarginRequest, ScheduleMargin.class);
			for (Map.Entry<String, Object> entryField : scheduleMargin.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ScheduleMargin.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			scheduleMarginRepo.save(scheduleMargin);
			return mapToScheduleMarginResponse(scheduleMargin);
		} else {
			throw new AlreadyExistsException("ScheduleMargin with this name already exists");
		}
	}

	@Override
	public ScheduleMarginResponse getScheduleMarginById(Long id) throws ResourceNotFoundException {
		ScheduleMargin scheduleMargin = this.findScheduleMarginById(id);
		return mapToScheduleMarginResponse(scheduleMargin);
	}

	@Override
	public List<ScheduleMarginResponse> getAllScheduleMargin() {
		List<ScheduleMargin> scheduleMargin = scheduleMarginRepo.findAllByOrderByIdAsc();
		return scheduleMargin.stream().map(this::mapToScheduleMarginResponse).toList();
	}

	@Override
	public List<ScheduleMargin> findAll() {
		return scheduleMarginRepo.findAllByOrderByIdAsc();
	}

	@Override
	public ScheduleMarginResponse updateScheduleMargin(Long id, ScheduleMarginRequest scheduleMarginRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String exist = scheduleMarginRequest.getScheduleMarginName();
		String existCode = scheduleMarginRequest.getScheduleMarginCode();
		boolean exists = scheduleMarginRepo.existsByScheduleMarginCodeAndScheduleMarginNameAndIdNot(existCode, exist,
				id);
		if (!exists) {
			ScheduleMargin existingScheduleMargin = this.findScheduleMarginById(id);
			modelMapper.map(scheduleMarginRequest, existingScheduleMargin);
			for (Map.Entry<String, Object> entryField : existingScheduleMargin.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ScheduleMargin.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			scheduleMarginRepo.save(existingScheduleMargin);
			return mapToScheduleMarginResponse(existingScheduleMargin);
		} else {
			throw new AlreadyExistsException("ScheduleMargin with this name already exists");
		}
	}

	@Override
	public List<ScheduleMarginResponse> updateBulkStatusScheduleMarginId(List<Long> id)
			throws ResourceNotFoundException {
		List<ScheduleMargin> existingScheduleMargin = this.findAllScheMargById(id);
		for (ScheduleMargin scheduleMargin : existingScheduleMargin) {
			scheduleMargin.setScheduleMarginStatus(!scheduleMargin.getScheduleMarginStatus());
		}
		scheduleMarginRepo.saveAll(existingScheduleMargin);
		return existingScheduleMargin.stream().map(this::mapToScheduleMarginResponse).toList();
	}

	@Override
	public ScheduleMarginResponse updateStatusUsingScheduleMarginId(Long id) throws ResourceNotFoundException {
		ScheduleMargin existingScheduleMargin = this.findScheduleMarginById(id);
		existingScheduleMargin.setScheduleMarginStatus(!existingScheduleMargin.getScheduleMarginStatus());
		scheduleMarginRepo.save(existingScheduleMargin);
		return mapToScheduleMarginResponse(existingScheduleMargin);
	}

	@Override
	public void deleteScheduleMargin(Long id) throws ResourceNotFoundException {
		ScheduleMargin scheduleMargin = this.findScheduleMarginById(id);
		scheduleMarginRepo.deleteById(scheduleMargin.getId());
	}

	@Override
	public void deleteBatchScheduleMargin(List<Long> ids) throws ResourceNotFoundException {
		this.findAllScheMargById(ids);
		scheduleMarginRepo.deleteAllByIdInBatch(ids);
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

	private ScheduleMarginResponse mapToScheduleMarginResponse(ScheduleMargin scheduleMargin) {
		return modelMapper.map(scheduleMargin, ScheduleMarginResponse.class);
	}

	private ScheduleMargin findScheduleMarginById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ScheduleMargin> scheduleMargin = scheduleMarginRepo.findById(id);
		if (scheduleMargin.isEmpty()) {
			throw new ResourceNotFoundException(SCHEDULE_MARGIN_NOT_FOUND_MESSAGE);
		}
		return scheduleMargin.get();
	}

	private List<ScheduleMargin> findAllScheMargById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ScheduleMargin> margins = scheduleMarginRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> margins.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Schedule Margin with IDs " + missingIds + " not found.");
		}
		return margins;
	}

}