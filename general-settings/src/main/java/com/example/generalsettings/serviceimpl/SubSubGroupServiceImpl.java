package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
	private static final String SUB_CHILD_GROUP_NOT_FOUND_MESSAGE = null;

	private final SubSubGroupRepo subSubGroupRepo;
	private final MainGroupCodesRepo mainGroupCodesRepo;
	private final SubGroupCodesRepo subGroupCodesRepo;

	private final ModelMapper modelMapper;

	@Override
	public SubSubGroupResponse saveSubChildGroup(SubSubGroupRequest subSubGroupRequest) throws AlreadyExistsException {
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
		String name = subSubGroupRequest.getSubSubGroupName();
		String code = subSubGroupRequest.getSubSubGroupCode();
		boolean exists = subSubGroupRepo.existsBySubSubGroupCodeAndSubSubGroupNameAndIdNot(code, name, subId);
		if (!exists) {
			SubSubGroup existSubChildGroup1 = this.findSubChildGroupById(subId);
			existSubChildGroup1.setId(subId);
			existSubChildGroup1.setSubSubGroupCode(code);
			existSubChildGroup1.setSubSubGroupName(name);
			existSubChildGroup1.setSubSubGroupStatus(subSubGroupRequest.getSubSubGroupStatus());
			MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(subSubGroupRequest.getMainGroupCodesId());
			SubGroupCodes subGroupCodes = this.findSubGroupCodesById(subSubGroupRequest.getSubGroupId());
			existSubChildGroup1.setMainGroupCodesId(mainGroupCodes);
			existSubChildGroup1.setSubGroupCodesId(subGroupCodes);
			subSubGroupRepo.save(existSubChildGroup1);
			return mapToSubChildGroupResponse(existSubChildGroup1);
		} else {
			throw new AlreadyExistsException("SubChildGroup with this name already exists");
		}
	}

	@Override
	public List<SubSubGroupResponse> updateBulkStatusSubChildGroupId(List<Long> id) throws ResourceNotFoundException {
		List<SubSubGroup> existingSubChildGroup = this.findAllSubChildGrpById(id);
		for (SubSubGroup subChildGroup : existingSubChildGroup) {
			subChildGroup.setSubSubGroupStatus(!subChildGroup.getSubSubGroupStatus());
		}
		subSubGroupRepo.saveAll(existingSubChildGroup);
		return existingSubChildGroup.stream().map(this::mapToSubChildGroupResponse).toList();
	}

	@Override
	public SubSubGroupResponse updateStatusUsingSubChildGroupId(Long id) throws ResourceNotFoundException {
		SubSubGroup existingSubChildGroup = this.findSubChildGroupById(id);
		existingSubChildGroup.setSubSubGroupStatus(!existingSubChildGroup.getSubSubGroupStatus());
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
			throw new ResourceNotFoundException(SUB_CHILD_GROUP_NOT_FOUND_MESSAGE);
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
