package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;

import jakarta.validation.Valid;

public interface ReferenceTypeService {

	ReferenceTypeResponse saveReferenceType(@Valid ReferenceTypeRequest referenceTypeRequest)throws ResourceNotFoundException, AlreadyExistsException;

	ReferenceTypeResponse updateReferenceType(Long id, ReferenceTypeRequest referenceTypeRequest)throws ResourceNotFoundException, AlreadyExistsException;

	ReferenceTypeResponse getReferenceTypeById(Long id)throws ResourceNotFoundException;

	void deleteReferenceType(Long id)throws ResourceNotFoundException;

	ReferenceTypeResponse updateStatusUsingReferenceTypeId(Long id)throws ResourceNotFoundException;

	List<ReferenceTypeResponse> updateBulkStatusReferenceTypeId(List<Long> id);

	List<ReferenceTypeResponse> getAllReferenceType();

}
