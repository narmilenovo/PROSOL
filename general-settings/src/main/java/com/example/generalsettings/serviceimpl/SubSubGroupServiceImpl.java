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
import com.example.generalsettings.entity.SubGroupCodes;
import com.example.generalsettings.entity.SubSubGroup;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.MainGroupCodesRepo;
import com.example.generalsettings.repo.SubGroupCodesRepo;
import com.example.generalsettings.repo.SubSubGroupRepo;
import com.example.generalsettings.request.SubSubGroupRequest;
import com.example.generalsettings.response.SubSubGroupResponse;
import com.example.generalsettings.service.SubSubGroupService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubSubGroupServiceImpl implements SubSubGroupService {

	private final SubSubGroupRepo subSubGroupRepo;
	private final MainGroupCodesRepo mainGroupCodesRepo;
	private final SubGroupCodesRepo subGroupCodesRepo;

	private final ModelMapper modelMapper;

	@Override
	public SubSubGroupResponse saveSubChildGroup(SubSubGroupRequest subSubGroupRequest) throws AlreadyExistsException {
		Helpers.inputTitleCase(subSubGroupRequest);
		boolean exists = subSubGroupRepo.existsBySubSubGroupCodeAndSubSubGroupName(
				subSubGroupRequest.getSubSubGroupCode(), subSubGroupRequest.getSubSubGroupName());
		if (!exists) {
			SubSubGroup subChildGroup = modelMapper.map(subSubGroupRequest, SubSubGroup.class);
			subChildGroup.setId(null);
			MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(subSubGroupRequest.getMainGroupCodesId());
			SubGroupCodes subGroupCodes = this.findSubGroupCodesById(subSubGroupRequest.getSubGroupId());
			subChildGroup.setMainGroupCodesId(mainGroupCodes);
			subChildGroup.setSubGroupCodesId(subGroupCodes);
			SubSubGroup saved = subSubGroupRepo.save(subChildGroup);
			return mapToSubChildGroupResponse(saved);
		} else {
			throw new AlreadyExistsException("SubChildGroup with this name already exists");
		}
	}

	@Override
	public SubSubGroupResponse getSubChildGroupById(Long id) throws ResourceNotFoundException {
		SubSubGroup subChildGroup = this.findSubChildGroupById(id);
		return mapToSubChildGroupResponse(subChildGroup);
	}

	@Override
	public List<SubSubGroupResponse> getAllSubChildGroup() {
		List<SubSubGroup> subChildGroup = subSubGroupRepo.findAllByOrderByIdAsc();
		return subChildGroup.stream().map(this::mapToSubChildGroupResponse).toList();
	}

	@Override
	public List<SubSubGroup> findAll() {
		return subSubGroupRepo.findAllByOrderByIdAsc();
	}

	@Override
	public SubSubGroupResponse updateSubChildGroup(Long subId, SubSubGroupRequest subSubGroupRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(subId);
		Helpers.inputTitleCase(subSubGroupRequest);
		String name = subSubGroupRequest.getSubSubGroupName();
		String code = subSubGroupRequest.getSubSubGroupCode();
		boolean exists = subSubGroupRepo.existsBySubSubGroupCodeAndSubSubGroupNameAndIdNot(code, name, subId);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			SubSubGroup existSubChildGroup = this.findSubChildGroupById(subId);
			if (!existSubChildGroup.getSubSubGroupCode().equals(code)) {
				auditFields
						.add(new AuditFields(null, "SubSubGroup Code", existSubChildGroup.getSubSubGroupCode(), code));
				existSubChildGroup.setSubSubGroupCode(code);
			}
			if (!existSubChildGroup.getSubSubGroupName().equals(name)) {
				auditFields
						.add(new AuditFields(null, "SubSubGroup Name", existSubChildGroup.getSubSubGroupName(), name));
				existSubChildGroup.setSubSubGroupName(name);
			}
			if (!existSubChildGroup.getSubSubGroupStatus().equals(subSubGroupRequest.getSubSubGroupStatus())) {
				auditFields.add(new AuditFields(null, "SubSubGroup Status", existSubChildGroup.getSubSubGroupStatus(),
						subSubGroupRequest.getSubSubGroupStatus()));
				existSubChildGroup.setSubSubGroupStatus(subSubGroupRequest.getSubSubGroupStatus());
			}
			if (!existSubChildGroup.getMainGroupCodesId().getId().equals(subSubGroupRequest.getMainGroupCodesId())) {
				auditFields.add(new AuditFields(null, "MainGroup Code", existSubChildGroup.getMainGroupCodesId(),
						subSubGroupRequest.getMainGroupCodesId()));
				MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(subSubGroupRequest.getMainGroupCodesId());
				existSubChildGroup.setMainGroupCodesId(mainGroupCodes);
			}
			if (!existSubChildGroup.getSubGroupCodesId().getId().equals(subSubGroupRequest.getSubGroupId())) {
				auditFields.add(new AuditFields(null, "Sub Group Code", existSubChildGroup.getSubGroupCodesId(),
						subSubGroupRequest.getSubGroupId()));
				SubGroupCodes subGroupCodes = this.findSubGroupCodesById(subSubGroupRequest.getSubGroupId());
				existSubChildGroup.setSubGroupCodesId(subGroupCodes);
			}
			existSubChildGroup.updateAuditHistory(auditFields);
			subSubGroupRepo.save(existSubChildGroup);
			return mapToSubChildGroupResponse(existSubChildGroup);
		} else {
			throw new AlreadyExistsException("SubChildGroup with this name already exists");
		}
	}

	@Override
	public List<SubSubGroupResponse> updateBulkStatusSubChildGroupId(List<Long> id) throws ResourceNotFoundException {
		List<SubSubGroup> existingSubChildGroupList = this.findAllSubChildGrpById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingSubChildGroupList.forEach(existingSubChildGroup -> {
			if (existingSubChildGroup.getSubSubGroupStatus() != null) {
				auditFields.add(new AuditFields(null, "SubSubGroup Status",
						existingSubChildGroup.getSubSubGroupStatus(), !existingSubChildGroup.getSubSubGroupStatus()));
				existingSubChildGroup.setSubSubGroupStatus(!existingSubChildGroup.getSubSubGroupStatus());
			}
			existingSubChildGroup.updateAuditHistory(auditFields);
		});
		subSubGroupRepo.saveAll(existingSubChildGroupList);
		return existingSubChildGroupList.stream().map(this::mapToSubChildGroupResponse).toList();
	}

	@Override
	public SubSubGroupResponse updateStatusUsingSubChildGroupId(Long id) throws ResourceNotFoundException {
		SubSubGroup existingSubChildGroup = this.findSubChildGroupById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingSubChildGroup.getSubSubGroupStatus() != null) {
			auditFields.add(new AuditFields(null, "SubSubGroup Status", existingSubChildGroup.getSubSubGroupStatus(),
					!existingSubChildGroup.getSubSubGroupStatus()));
			existingSubChildGroup.setSubSubGroupStatus(!existingSubChildGroup.getSubSubGroupStatus());
		}
		existingSubChildGroup.updateAuditHistory(auditFields);
		subSubGroupRepo.save(existingSubChildGroup);
		return mapToSubChildGroupResponse(existingSubChildGroup);
	}

	@Override
	public void deleteSubChildGroup(Long id) throws ResourceNotFoundException {
		SubSubGroup subChildGroup = this.findSubChildGroupById(id);
		subSubGroupRepo.deleteById(subChildGroup.getId());
	}

	@Override
	public void deleteBatchSubSubGroup(List<Long> ids) throws ResourceNotFoundException {
		this.findAllSubChildGrpById(ids);
		subSubGroupRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertSubChildGroupCodesListToMap(List<SubSubGroup> subChildGroupList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (SubSubGroup unit : subChildGroupList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getSubGroupCodesId());
			data.put("Name", unit.getSubSubGroupName());
			data.put("Status", unit.getSubSubGroupStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private MainGroupCodes findMainGroupCodesById(Long mainId) {
		Optional<MainGroupCodes> fetchplantOptional = mainGroupCodesRepo.findById(mainId);
		return fetchplantOptional.orElse(null);

	}

	private SubGroupCodes findSubGroupCodesById(Long subId) {
		Optional<SubGroupCodes> fetchStorageOptional1 = subGroupCodesRepo.findById(subId);
		return fetchStorageOptional1.orElse(null);

	}

	private SubSubGroupResponse mapToSubChildGroupResponse(SubSubGroup subChildGroup) {
		return modelMapper.map(subChildGroup, SubSubGroupResponse.class);
	}

	private SubSubGroup findSubChildGroupById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<SubSubGroup> subChildGroup = subSubGroupRepo.findById(id);
		if (subChildGroup.isEmpty()) {
			throw new ResourceNotFoundException("Sub Sub Child Group with id " + id + " not found");
		}
		return subChildGroup.get();
	}

	private List<SubSubGroup> findAllSubChildGrpById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<SubSubGroup> subChildGroupCodes = subSubGroupRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> subChildGroupCodes.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Sub Sub Child Group with IDs " + missingIds + " not found.");
		}
		return subChildGroupCodes;
	}
}
