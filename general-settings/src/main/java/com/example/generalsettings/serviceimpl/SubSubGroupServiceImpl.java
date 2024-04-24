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
import com.example.generalsettings.entity.SubGroupCodes;
import com.example.generalsettings.entity.SubSubGroup;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.SubSubGroupCodeMap;
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

	private final SubSubGroupCodeMap subSubGroupCodeMapper;

	@Override
	public SubSubGroupResponse saveSubChildGroup(SubSubGroupRequest subSubGroupRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(subSubGroupRequest);
		String subSubGroupCode = subSubGroupRequest.getSubSubGroupCode();
		String subSubGroupName = subSubGroupRequest.getSubSubGroupName();
		if (subSubGroupRepo.existsBySubSubGroupCodeAndSubSubGroupName(subSubGroupCode, subSubGroupName)) {
			throw new AlreadyExistsException("SubChildGroup with this name already exists");
		}
		SubSubGroup subChildGroup = subSubGroupCodeMapper.mapToSubSubGroup(subSubGroupRequest);
		subChildGroup.setId(null);
		MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(subSubGroupRequest.getMainGroupCodesId());
		SubGroupCodes subGroupCodes = this.findSubGroupCodesById(subSubGroupRequest.getSubGroupId());
		subChildGroup.setMainGroupCodes(mainGroupCodes);
		subChildGroup.setSubGroupCodes(subGroupCodes);
		SubSubGroup saved = subSubGroupRepo.save(subChildGroup);
		return subSubGroupCodeMapper.mapToSubChildGroupResponse(saved);
	}

	@Override
	public SubSubGroupResponse getSubChildGroupById(Long id) throws ResourceNotFoundException {
		SubSubGroup subChildGroup = this.findSubChildGroupById(id);
		return subSubGroupCodeMapper.mapToSubChildGroupResponse(subChildGroup);
	}

	@Override
	public List<SubSubGroupResponse> getAllSubChildGroup() {
		return subSubGroupRepo.findAllByOrderByIdAsc().stream().map(subSubGroupCodeMapper::mapToSubChildGroupResponse)
				.toList();
	}

	@Override
	public List<SubSubGroup> findAll() {
		return subSubGroupRepo.findAllByOrderByIdAsc();
	}

	@Override
	public SubSubGroupResponse updateSubChildGroup(Long subId, SubSubGroupRequest subSubGroupRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
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
			if (!existSubChildGroup.getMainGroupCodes().getId().equals(subSubGroupRequest.getMainGroupCodesId())) {
				auditFields.add(new AuditFields(null, "MainGroup Code", existSubChildGroup.getMainGroupCodes(),
						subSubGroupRequest.getMainGroupCodesId()));
				MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(subSubGroupRequest.getMainGroupCodesId());
				existSubChildGroup.setMainGroupCodes(mainGroupCodes);
			}
			if (!existSubChildGroup.getSubGroupCodes().getId().equals(subSubGroupRequest.getSubGroupId())) {
				auditFields.add(new AuditFields(null, "Sub Group Code", existSubChildGroup.getSubGroupCodes(),
						subSubGroupRequest.getSubGroupId()));
				SubGroupCodes subGroupCodes = this.findSubGroupCodesById(subSubGroupRequest.getSubGroupId());
				existSubChildGroup.setSubGroupCodes(subGroupCodes);
			}
			existSubChildGroup.updateAuditHistory(auditFields);
			subSubGroupRepo.save(existSubChildGroup);
			return subSubGroupCodeMapper.mapToSubChildGroupResponse(existSubChildGroup);
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
		return existingSubChildGroupList.stream().map(subSubGroupCodeMapper::mapToSubChildGroupResponse).toList();
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
		return subSubGroupCodeMapper.mapToSubChildGroupResponse(existingSubChildGroup);
	}

	@Override
	public void deleteSubChildGroup(Long id) throws ResourceNotFoundException {
		SubSubGroup subChildGroup = this.findSubChildGroupById(id);
		if (subChildGroup != null) {
			subSubGroupRepo.delete(subChildGroup);
		}
	}

	@Override
	public void deleteBatchSubSubGroup(List<Long> ids) throws ResourceNotFoundException {
		List<SubSubGroup> subSubGroups = findAllSubChildGrpById(ids);
		if (!subSubGroups.isEmpty()) {
			subSubGroupRepo.deleteAll(subSubGroups);
		}
	}

	@Override
	public List<Map<String, Object>> convertSubChildGroupCodesListToMap(List<SubSubGroup> subChildGroupList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (SubSubGroup unit : subChildGroupList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getSubGroupCodes());
			data.put("Name", unit.getSubSubGroupName());
			data.put("Status", unit.getSubSubGroupStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private MainGroupCodes findMainGroupCodesById(Long mainId) throws ResourceNotFoundException {
		return mainGroupCodesRepo.findById(mainId)
				.orElseThrow(() -> new ResourceNotFoundException("Main Child Group with id " + mainId + " not found"));
	}

	private SubGroupCodes findSubGroupCodesById(Long subId) throws ResourceNotFoundException {
		return subGroupCodesRepo.findById(subId).orElseThrow(
				() -> new ResourceNotFoundException("Sub Sub Child Group with id " + subId + " not found"));

	}

	private SubSubGroup findSubChildGroupById(Long id) throws ResourceNotFoundException {
		return subSubGroupRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sub Sub Child Group with id " + id + " not found"));
	}

	private List<SubSubGroup> findAllSubChildGrpById(List<Long> ids) throws ResourceNotFoundException {

		List<SubSubGroup> subChildGroupCodes = subSubGroupRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);

		List<SubSubGroup> foundSubChildGroupCodes = subChildGroupCodes.stream()
				.filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Sub Sub Child Group with IDs " + missingIds + " not found.");
		}

		return foundSubChildGroupCodes;
	}

}
