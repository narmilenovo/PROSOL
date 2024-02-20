package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.MainGroupCodesRepo;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;
import com.example.generalsettings.service.MainGroupCodesService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainGroupCodesServiceImpl implements MainGroupCodesService {

	private final MainGroupCodesRepo mainGroupCodesRepo;

	private final ModelMapper modelMapper;

	public static final String MAIN_GROUP_CODE_NOT_FOUND_MESSAGE = null;

	@Override
	public MainGroupCodesResponse saveMainGroupCodes(MainGroupCodesRequest mainGroupCodesRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(mainGroupCodesRequest);
		boolean exists = mainGroupCodesRepo.existsByMainGroupCodeAndMainGroupName(
				mainGroupCodesRequest.getMainGroupCode(), mainGroupCodesRequest.getMainGroupName());
		if (!exists) {
			MainGroupCodes mainGroupCodes = modelMapper.map(mainGroupCodesRequest, MainGroupCodes.class);
			mainGroupCodesRepo.save(mainGroupCodes);
			return mapToMainGroupCodesResponse(mainGroupCodes);
		} else {
			throw new AlreadyExistsException("MainGroupCodes with this name already exists");
		}
	}

	@Override
	public MainGroupCodesResponse getMainGroupCodesById(Long id) throws ResourceNotFoundException {
		MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(id);
		return mapToMainGroupCodesResponse(mainGroupCodes);
	}

	@Override
	public List<MainGroupCodesResponse> getAllMainGroupCodes() {
		List<MainGroupCodes> mainGroupCodes = mainGroupCodesRepo.findAllByOrderByIdAsc();
		return mainGroupCodes.stream().map(this::mapToMainGroupCodesResponse).toList();
	}

	@Override
	public List<MainGroupCodes> findAll() {
		return mainGroupCodesRepo.findAllByOrderByIdAsc();
	}

	@Override
	public MainGroupCodesResponse updateMainGroupCodes(Long id, MainGroupCodesRequest mainGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(mainGroupCodesRequest);
		String name = mainGroupCodesRequest.getMainGroupName();
		String code = mainGroupCodesRequest.getMainGroupCode();
		boolean exists = mainGroupCodesRepo.existsByMainGroupCodeAndMainGroupNameAndIdNot(code, name, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			MainGroupCodes existingMainGroupCodes = this.findMainGroupCodesById(id);
			if (!existingMainGroupCodes.getMainGroupCode().equals(code)) {
				auditFields
						.add(new AuditFields(null, "Main Group Code", existingMainGroupCodes.getMainGroupCode(), code));
				existingMainGroupCodes.setMainGroupCode(code);
			}
			if (!existingMainGroupCodes.getMainGroupName().equals(name)) {
				auditFields
						.add(new AuditFields(null, "Main Group Name", existingMainGroupCodes.getMainGroupName(), name));
				existingMainGroupCodes.setMainGroupName(name);
			}
			if (!existingMainGroupCodes.getMainGroupStatus().equals(mainGroupCodesRequest.getMainGroupStatus())) {
				auditFields.add(new AuditFields(null, "Main Group Status", existingMainGroupCodes.getMainGroupStatus(),
						mainGroupCodesRequest.getMainGroupStatus()));
				existingMainGroupCodes.setMainGroupStatus(mainGroupCodesRequest.getMainGroupStatus());
			}
			existingMainGroupCodes.updateAuditHistory(auditFields);
			mainGroupCodesRepo.save(existingMainGroupCodes);
			return mapToMainGroupCodesResponse(existingMainGroupCodes);
		} else {
			throw new AlreadyExistsException("MainGroupCodes with this name already exists");
		}
	}

	@Override
	public List<MainGroupCodesResponse> updateBulkStatusMainGroupCodesId(List<Long> id)
			throws ResourceNotFoundException {
		List<MainGroupCodes> existingMainGroupCodes = this.findAllMainGrpById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingMainGroupCodes.forEach(existingMainGroupCode -> {

			if (existingMainGroupCode.getMainGroupStatus() != null) {
				auditFields.add(new AuditFields(null, "Main Group Status", existingMainGroupCode.getMainGroupStatus(),
						!existingMainGroupCode.getMainGroupStatus()));
				existingMainGroupCode.setMainGroupStatus(!existingMainGroupCode.getMainGroupStatus());
			}
			existingMainGroupCode.updateAuditHistory(auditFields);

		});
		mainGroupCodesRepo.saveAll(existingMainGroupCodes);
		return existingMainGroupCodes.stream().map(this::mapToMainGroupCodesResponse).toList();
	}

	@Override
	public MainGroupCodesResponse updateStatusUsingMainGroupCodesId(Long id) throws ResourceNotFoundException {
		MainGroupCodes existingMainGroupCode = this.findMainGroupCodesById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingMainGroupCode.getMainGroupStatus() != null) {
			auditFields.add(new AuditFields(null, "Main Group Status", existingMainGroupCode.getMainGroupStatus(),
					!existingMainGroupCode.getMainGroupStatus()));
			existingMainGroupCode.setMainGroupStatus(!existingMainGroupCode.getMainGroupStatus());
		}
		existingMainGroupCode.updateAuditHistory(auditFields);
		mainGroupCodesRepo.save(existingMainGroupCode);
		return mapToMainGroupCodesResponse(existingMainGroupCode);
	}

	@Override
	public void deleteMainGroupCodes(Long id) throws ResourceNotFoundException {
		MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(id);
		mainGroupCodesRepo.deleteById(mainGroupCodes.getId());
	}

	@Override
	public void deleteBatchMainGroupCodes(List<Long> ids) throws ResourceNotFoundException {
		this.findAllMainGrpById(ids);
		mainGroupCodesRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertMainGroupCodesListToMap(List<MainGroupCodes> mainGroupCodesList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (MainGroupCodes unit : mainGroupCodesList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getId());
			data.put("Name", unit.getMainGroupName());
			data.put("Status", unit.getMainGroupStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private MainGroupCodesResponse mapToMainGroupCodesResponse(MainGroupCodes mainGroupCodes) {
		return modelMapper.map(mainGroupCodes, MainGroupCodesResponse.class);
	}

	private MainGroupCodes findMainGroupCodesById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<MainGroupCodes> mainGroupCodes = mainGroupCodesRepo.findById(id);
		if (mainGroupCodes.isEmpty()) {
			throw new ResourceNotFoundException(MAIN_GROUP_CODE_NOT_FOUND_MESSAGE);
		}
		return mainGroupCodes.get();
	}

	private List<MainGroupCodes> findAllMainGrpById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<MainGroupCodes> codes = mainGroupCodesRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> codes.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Main Group Codes with IDs " + missingIds + " not found.");
		}
		return codes;
	}
}
