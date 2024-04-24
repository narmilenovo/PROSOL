package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.MainGroupCodeMap;
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

	private final MainGroupCodeMap mainGroupCodeMapper;

	@Override
	public MainGroupCodesResponse saveMainGroupCodes(MainGroupCodesRequest mainGroupCodesRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(mainGroupCodesRequest);
		String mainGroupCode = mainGroupCodesRequest.getMainGroupCode();
		String mainGroupName = mainGroupCodesRequest.getMainGroupName();
		if (mainGroupCodesRepo.existsByMainGroupCodeAndMainGroupName(mainGroupCode, mainGroupName)) {
			throw new AlreadyExistsException("MainGroupCodes with this name already exists");
		}
		MainGroupCodes mainGroupCodes = mainGroupCodeMapper.mapToMainGroupCodes(mainGroupCodesRequest);
		mainGroupCodesRepo.save(mainGroupCodes);
		return mainGroupCodeMapper.mapToMainGroupCodesResponse(mainGroupCodes);
	}

	@Override
	public MainGroupCodesResponse getMainGroupCodesById(Long id) throws ResourceNotFoundException {
		MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(id);
		return mainGroupCodeMapper.mapToMainGroupCodesResponse(mainGroupCodes);
	}

	@Override
	public List<MainGroupCodesResponse> getAllMainGroupCodes() {
		return mainGroupCodesRepo.findAllByOrderByIdAsc().stream().map(mainGroupCodeMapper::mapToMainGroupCodesResponse)
				.toList();
	}

	@Override
	public List<MainGroupCodes> findAll() {
		return mainGroupCodesRepo.findAllByOrderByIdAsc();
	}

	@Override
	public MainGroupCodesResponse updateMainGroupCodes(Long id, MainGroupCodesRequest mainGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
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
			return mainGroupCodeMapper.mapToMainGroupCodesResponse(existingMainGroupCodes);
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
		return existingMainGroupCodes.stream().map(mainGroupCodeMapper::mapToMainGroupCodesResponse).toList();
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
		return mainGroupCodeMapper.mapToMainGroupCodesResponse(existingMainGroupCode);
	}

	@Override
	public void deleteMainGroupCodes(Long id) throws ResourceNotFoundException {
		MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(id);
		if (mainGroupCodes != null) {
			mainGroupCodesRepo.delete(mainGroupCodes);
		}
	}

	@Override
	public void deleteBatchMainGroupCodes(List<Long> ids) throws ResourceNotFoundException {
		List<MainGroupCodes> mainGroupCodes = this.findAllMainGrpById(ids);
		if (!mainGroupCodes.isEmpty()) {
			mainGroupCodesRepo.deleteAll(mainGroupCodes);
		}
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

	private MainGroupCodes findMainGroupCodesById(Long id) throws ResourceNotFoundException {
		return mainGroupCodesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Main Group Code with" + id + " not found !!!"));
	}

	private List<MainGroupCodes> findAllMainGrpById(List<Long> ids) throws ResourceNotFoundException {
		List<MainGroupCodes> codes = mainGroupCodesRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);
		List<MainGroupCodes> foundCodes = codes.stream().filter(code -> idSet.contains(code.getId())).toList();
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();
		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Main Group Codes with IDs " + missingIds + " not found.");
		}

		return foundCodes;
	}

}
