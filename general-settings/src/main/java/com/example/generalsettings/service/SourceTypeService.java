package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.SourceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;

import jakarta.validation.Valid;

public interface SourceTypeService {

	SourceTypeResponse saveSourceType(@Valid SourceTypeRequest sourceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	SourceTypeResponse getSourceTypeById(Long id) throws ResourceNotFoundException;

	List<SourceTypeResponse> getAllSourceType();

	List<SourceType> findAll();

	SourceTypeResponse updateSourceType(Long id, SourceTypeRequest sourceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	SourceTypeResponse updateStatusUsingSourceTypeId(Long id) throws ResourceNotFoundException;

	List<SourceTypeResponse> updateBulkStatusSourceTypeId(List<Long> id) throws ResourceNotFoundException;

	void deleteSourceType(Long id) throws ResourceNotFoundException;

	void deleteBatchSourceType(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertSourceTypeListToMap(List<SourceType> sourceTypeReport);
}
