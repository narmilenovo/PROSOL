package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.*;
import com.example.generalsettings.request.SubMainGroupRequest;
import com.example.generalsettings.response.SubMainGroupResponse;

import jakarta.validation.Valid;

public interface SubMainGroupService {

	List<SubMainGroupResponse> getAllSubMainGroup();

	SubMainGroupResponse updateSubMainGroup(Long id, SubMainGroupRequest subMainGroupRequest)throws ResourceNotFoundException, AlreadyExistsException;

	void deleteSubMainGroup(Long id)throws ResourceNotFoundException;

	SubMainGroupResponse saveSubMainGroup(@Valid SubMainGroupRequest subMainGroupRequest)throws ResourceNotFoundException, AlreadyExistsException;

	SubMainGroupResponse getSubMainGroupById(Long id)throws ResourceNotFoundException;

	SubMainGroupResponse updateStatusUsingSubMainGroupId(Long id)throws ResourceNotFoundException;

	List<SubMainGroupResponse> updateBulkStatusSubMainGroupId(List<Long> id);

}
