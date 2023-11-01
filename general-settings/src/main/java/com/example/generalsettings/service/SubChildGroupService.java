package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.SubChildGroupRequest;
import com.example.generalsettings.response.SubChildGroupResponse;

import jakarta.validation.Valid;

public interface SubChildGroupService {

	List<SubChildGroupResponse> getAllSubChildGroup();

	SubChildGroupResponse updateSubChildGroup(Long id, SubChildGroupRequest subChildGroupRequest)throws ResourceNotFoundException, AlreadyExistsException ;

	SubChildGroupResponse saveSubChildGroup(@Valid SubChildGroupRequest subChildGroupRequest)throws ResourceNotFoundException, AlreadyExistsException ;

	SubChildGroupResponse getSubChildGroupById(Long id)throws ResourceNotFoundException;

	void deleteSubChildGroup(Long id)throws ResourceNotFoundException;

	SubChildGroupResponse updateStatusUsingSubChildGroupId(Long id)throws ResourceNotFoundException;

	List<SubChildGroupResponse> updateBulkStatusSubChildGroupId(List<Long> id);

}
