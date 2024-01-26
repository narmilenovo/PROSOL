package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.SubGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.SubGroupCodesRequest;
import com.example.generalsettings.response.SubGroupCodesResponse;

import jakarta.validation.Valid;

public interface SubGroupService {

	SubGroupCodesResponse saveSubMainGroup(@Valid SubGroupCodesRequest subGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	SubGroupCodesResponse getSubMainGroupById(Long id) throws ResourceNotFoundException;

	List<SubGroupCodesResponse> getAllSubMainGroup();

	List<SubGroupCodes> findAll();

	SubGroupCodesResponse updateSubMainGroup(Long id, SubGroupCodesRequest subGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	SubGroupCodesResponse updateStatusUsingSubMainGroupId(Long id) throws ResourceNotFoundException;

	List<SubGroupCodesResponse> updateBulkStatusSubMainGroupId(List<Long> id) throws ResourceNotFoundException;

	void deleteSubMainGroup(Long id) throws ResourceNotFoundException;

	void deleteBatchSubGroupCodes(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertSubMainGroupCodesListToMap(List<SubGroupCodes> subGroupCodesReport);

}
