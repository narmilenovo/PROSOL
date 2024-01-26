package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.SubSubGroup;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.SubSubGroupRequest;
import com.example.generalsettings.response.SubSubGroupResponse;

import jakarta.validation.Valid;

public interface SubSubGroupService {

	SubSubGroupResponse saveSubChildGroup(@Valid SubSubGroupRequest subSubGroupRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	SubSubGroupResponse getSubChildGroupById(Long id) throws ResourceNotFoundException;

	List<SubSubGroupResponse> getAllSubChildGroup();

	List<SubSubGroup> findAll();

	SubSubGroupResponse updateSubChildGroup(Long id, SubSubGroupRequest subSubGroupRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	SubSubGroupResponse updateStatusUsingSubChildGroupId(Long id) throws ResourceNotFoundException;

	List<SubSubGroupResponse> updateBulkStatusSubChildGroupId(List<Long> id) throws ResourceNotFoundException;

	void deleteSubChildGroup(Long id) throws ResourceNotFoundException;

	void deleteBatchSubSubGroup(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertSubChildGroupCodesListToMap(List<SubSubGroup> subChildGroupCodesReport);

}
