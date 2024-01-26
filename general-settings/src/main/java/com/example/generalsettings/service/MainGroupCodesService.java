package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;

import jakarta.validation.Valid;

public interface MainGroupCodesService {

	MainGroupCodesResponse saveMainGroupCodes(@Valid MainGroupCodesRequest mainGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	MainGroupCodesResponse getMainGroupCodesById(Long id) throws ResourceNotFoundException;

	List<MainGroupCodesResponse> getAllMainGroupCodes();

	List<MainGroupCodes> findAll();

	MainGroupCodesResponse updateMainGroupCodes(Long id, MainGroupCodesRequest mainGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	MainGroupCodesResponse updateStatusUsingMainGroupCodesId(Long id) throws ResourceNotFoundException;

	List<MainGroupCodesResponse> updateBulkStatusMainGroupCodesId(List<Long> id) throws ResourceNotFoundException;

	void deleteMainGroupCodes(Long id) throws ResourceNotFoundException;

	void deleteBatchMainGroupCodes(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertMainGroupCodesListToMap(List<MainGroupCodes> mainGroupCodesReport);
}
