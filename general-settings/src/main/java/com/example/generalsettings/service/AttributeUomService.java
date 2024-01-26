package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;

import jakarta.validation.Valid;

public interface AttributeUomService {

	AttributeUomResponse saveAttributeUom(@Valid AttributeUomRequest attributeUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	AttributeUomResponse updateAttributeUom(Long id, AttributeUomRequest attributeUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	AttributeUomResponse getAttributeUomById(Long id) throws ResourceNotFoundException;

	List<AttributeUomResponse> getAllAttributeUom();

	AttributeUomResponse updateStatusUsingAttributeUomId(Long id) throws ResourceNotFoundException;

	List<AttributeUomResponse> updateBulkStatusAttributeUomId(List<Long> id) throws ResourceNotFoundException;

	void deleteAttributeUom(Long id) throws ResourceNotFoundException;

	void deleteBatchAttributeUom(List<Long> ids) throws ResourceNotFoundException;
}
