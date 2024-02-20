package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.entity.SubGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.MainGroupCodesRepo;
import com.example.generalsettings.repo.SubGroupCodesRepo;
import com.example.generalsettings.request.SubGroupCodesRequest;
import com.example.generalsettings.response.SubGroupCodesResponse;
import com.example.generalsettings.service.SubGroupService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubGroupServiceImpl implements SubGroupService {

	private final SubGroupCodesRepo subGroupCodesRepo;

	private final MainGroupCodesRepo mainGroupCodesRepo;

	private final ModelMapper modelMapper = new ModelMapper();

	@Override
	public SubGroupCodesResponse saveSubMainGroup(SubGroupCodesRequest subGroupCodesRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(subGroupCodesRequest);
		boolean exists = subGroupCodesRepo.existsBySubGroupCodeAndSubGroupName(subGroupCodesRequest.getSubGroupCode(),
				subGroupCodesRequest.getSubGroupName());
		if (!exists) {
			SubGroupCodes subGroupCodes = modelMapper.map(subGroupCodesRequest, SubGroupCodes.class);
			subGroupCodes.setId(null);
			MainGroupCodes mainGroupCodes = this.findMainGroupCodesByid(subGroupCodesRequest.getMainGroupCodesId());
			subGroupCodes.setMainGroupCodesId(mainGroupCodes);
			subGroupCodesRepo.save(subGroupCodes);
			return mapToSubMainGroupResponse(subGroupCodes);
		} else {
			throw new AlreadyExistsException("SubGroupCodes with this name already exists");
		}
	}

	@Override
	public SubGroupCodesResponse getSubMainGroupById(Long id) throws ResourceNotFoundException {
		SubGroupCodes subGroupCodes = this.findSubMainGroupById(id);
		return mapToSubMainGroupResponse(subGroupCodes);
	}

	@Override
	public List<SubGroupCodes> findAll() {
		return subGroupCodesRepo.findAllByOrderByIdAsc();
	}

	@Override
	public List<SubGroupCodesResponse> getAllSubMainGroup() {
		List<SubGroupCodes> subGroupCodes = subGroupCodesRepo.findAllByOrderByIdAsc();
		return subGroupCodes.stream().map(this::mapToSubMainGroupResponse).toList();
	}

	@Override
	public List<SubGroupCodesResponse> getAllSubGroupCodesByMainGroupId(Long id) {
		List<SubGroupCodes> subGroupCodes = subGroupCodesRepo.findAllByMainGroupCodesId_Id(id);
		return subGroupCodes.stream().sorted(Comparator.comparing(SubGroupCodes::getId))
				.map(this::mapToSubMainGroupResponse).toList();
	}

	@Override
	public SubGroupCodesResponse updateSubMainGroup(Long id, SubGroupCodesRequest subGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(subGroupCodesRequest);
		String name = subGroupCodesRequest.getSubGroupName();
		String code = subGroupCodesRequest.getSubGroupCode();
		boolean exists = subGroupCodesRepo.existsBySubGroupCodeAndSubGroupNameAndIdNot(code, name, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			SubGroupCodes existingSubGroupCodes = this.findSubMainGroupById(id);
			if (!existingSubGroupCodes.getSubGroupCode().equals(code)) {
				auditFields.add(new AuditFields(null, "SubGroup Code", existingSubGroupCodes.getSubGroupCode(), code));
				existingSubGroupCodes.setSubGroupCode(code);
			}
			if (!existingSubGroupCodes.getSubGroupName().equals(name)) {
				auditFields.add(new AuditFields(null, "SubGroup Name", existingSubGroupCodes.getSubGroupName(), name));
				existingSubGroupCodes.setSubGroupName(name);
			}
			if (!existingSubGroupCodes.getSubGroupStatus().equals(subGroupCodesRequest.getSubGroupStatus())) {
				auditFields.add(new AuditFields(null, "SubGroup Status", existingSubGroupCodes.getSubGroupStatus(),
						subGroupCodesRequest.getSubGroupStatus()));
				existingSubGroupCodes.setSubGroupStatus(subGroupCodesRequest.getSubGroupStatus());
			}
			if (!existingSubGroupCodes.getMainGroupCodesId().getId()
					.equals(subGroupCodesRequest.getMainGroupCodesId())) {
				auditFields.add(new AuditFields(null, "MainGroup Code", existingSubGroupCodes.getMainGroupCodesId(),
						subGroupCodesRequest.getMainGroupCodesId()));
				MainGroupCodes mainGroupCodes = this.findMainGroupCodesByid(subGroupCodesRequest.getMainGroupCodesId());
				existingSubGroupCodes.setMainGroupCodesId(mainGroupCodes);
			}
			existingSubGroupCodes.updateAuditHistory(auditFields);
			subGroupCodesRepo.save(existingSubGroupCodes);
			return mapToSubMainGroupResponse(existingSubGroupCodes);
		} else {
			throw new AlreadyExistsException("SubGroupCodes with this name already exists");
		}
	}

	@Override
	public List<SubGroupCodesResponse> updateBulkStatusSubMainGroupId(List<Long> id) throws ResourceNotFoundException {
		List<SubGroupCodes> existingSubGroupCodes = this.findAllSubMainGrpById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingSubGroupCodes.forEach(existingSubGroupCode -> {
			if (existingSubGroupCode.getSubGroupStatus() != null) {
				auditFields.add(new AuditFields(null, "SubGroup Status", existingSubGroupCode.getSubGroupStatus(),
						!existingSubGroupCode.getSubGroupStatus()));
				existingSubGroupCode.setSubGroupStatus(!existingSubGroupCode.getSubGroupStatus());
			}
			existingSubGroupCode.updateAuditHistory(auditFields);
		});
		subGroupCodesRepo.saveAll(existingSubGroupCodes);
		return existingSubGroupCodes.stream().map(this::mapToSubMainGroupResponse).toList();
	}

	@Override
	public SubGroupCodesResponse updateStatusUsingSubMainGroupId(Long id) throws ResourceNotFoundException {
		SubGroupCodes existingSubGroupCode = this.findSubMainGroupById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingSubGroupCode.getSubGroupStatus() != null) {
			auditFields.add(new AuditFields(null, "SubGroup Status", existingSubGroupCode.getSubGroupStatus(),
					!existingSubGroupCode.getSubGroupStatus()));
			existingSubGroupCode.setSubGroupStatus(!existingSubGroupCode.getSubGroupStatus());
		}
		existingSubGroupCode.updateAuditHistory(auditFields);
		subGroupCodesRepo.save(existingSubGroupCode);
		return mapToSubMainGroupResponse(existingSubGroupCode);
	}

	@Override
	public void deleteSubMainGroup(Long id) throws ResourceNotFoundException {
		SubGroupCodes subGroupCodes = this.findSubMainGroupById(id);
		subGroupCodesRepo.deleteById(subGroupCodes.getId());
	}

	@Override
	public void deleteBatchSubGroupCodes(List<Long> ids) throws ResourceNotFoundException {
		this.findAllSubMainGrpById(ids);
		subGroupCodesRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertSubMainGroupCodesListToMap(List<SubGroupCodes> subGroupCodesList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (SubGroupCodes unit : subGroupCodesList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getMainGroupCodesId());
			data.put("Name", unit.getSubGroupName());
			data.put("Status", unit.getSubGroupStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private MainGroupCodes findMainGroupCodesByid(Long id) {
		Optional<MainGroupCodes> fetchplantOptional = mainGroupCodesRepo.findById(id);
		return fetchplantOptional.orElse(null);
	}

	private SubGroupCodesResponse mapToSubMainGroupResponse(SubGroupCodes subGroupCodes) {
		return modelMapper.map(subGroupCodes, SubGroupCodesResponse.class);
	}

	private SubGroupCodes findSubMainGroupById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<SubGroupCodes> subMainGroup = subGroupCodesRepo.findById(id);
		if (subMainGroup.isEmpty()) {
			throw new ResourceNotFoundException("Sub Group Codes with ID " + id + " not found");
		}
		return subMainGroup.get();
	}

	private List<SubGroupCodes> findAllSubMainGrpById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<SubGroupCodes> subGroupCodes = subGroupCodesRepo.findAllById(ids);

		List<Long> missingIds = ids.stream()
				.filter(id -> subGroupCodes.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Sub Group Codes with IDs " + missingIds + " not found.");
		}
		return subGroupCodes;
	}

}
