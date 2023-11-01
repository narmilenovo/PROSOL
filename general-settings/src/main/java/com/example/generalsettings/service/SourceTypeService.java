package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;

import jakarta.validation.Valid;

public interface SourceTypeService {

	SourceTypeResponse saveSourceType(@Valid SourceTypeRequest sourceTypeRequest)throws ResourceNotFoundException, AlreadyExistsException;

	SourceTypeResponse updateSourceType(Long id, SourceTypeRequest sourceTypeRequest)throws ResourceNotFoundException, AlreadyExistsException;

	SourceTypeResponse getSourceTypeById(Long id)throws ResourceNotFoundException;

	void deleteSourceType(Long id)throws ResourceNotFoundException;

	SourceTypeResponse updateStatusUsingSourceTypeId(Long id)throws ResourceNotFoundException;

	List<SourceTypeResponse> updateBulkStatusSourceTypeId(List<Long> id);

	List<SourceTypeResponse> getAllSourceType();

}
