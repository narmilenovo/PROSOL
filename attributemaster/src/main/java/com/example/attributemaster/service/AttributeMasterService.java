package com.example.attributemaster.service;

import java.util.List;

import com.example.attributemaster.client.AttributeMasterUomResponse;
import com.example.attributemaster.exception.AlreadyExistsException;
import com.example.attributemaster.exception.ResourceNotFoundException;
import com.example.attributemaster.request.AttributeMasterRequest;
import com.example.attributemaster.response.AttributeMasterResponse;

public interface AttributeMasterService {
	AttributeMasterResponse saveAttributeMaster(AttributeMasterRequest attributeMasterRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	AttributeMasterResponse getAttributeMasterById(Long id) throws ResourceNotFoundException;

	AttributeMasterUomResponse getAttributeMasterUomById(Long id) throws ResourceNotFoundException;

	List<AttributeMasterResponse> getAllAttributeMaster() throws ResourceNotFoundException;

	List<AttributeMasterUomResponse> getAllAttributeMasterUom() throws ResourceNotFoundException;

	AttributeMasterResponse updateAttributeMaster(Long id, AttributeMasterRequest attributeMasterRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	void deleteAttributeMaster(Long id) throws ResourceNotFoundException;

	void deleteBatchMaster(List<Long> ids) throws ResourceNotFoundException;

}
