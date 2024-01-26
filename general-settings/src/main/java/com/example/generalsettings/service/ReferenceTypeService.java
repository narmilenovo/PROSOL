package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.ReferenceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;

import jakarta.validation.Valid;

public interface ReferenceTypeService {

	ReferenceTypeResponse saveReferenceType(@Valid ReferenceTypeRequest referenceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ReferenceTypeResponse getReferenceTypeById(Long id) throws ResourceNotFoundException;

	List<ReferenceTypeResponse> getAllReferenceType();

	List<ReferenceType> findAll();

	ReferenceTypeResponse updateReferenceType(Long id, ReferenceTypeRequest referenceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	ReferenceTypeResponse updateStatusUsingReferenceTypeId(Long id) throws ResourceNotFoundException;

	List<ReferenceTypeResponse> updateBulkStatusReferenceTypeId(List<Long> id) throws ResourceNotFoundException;

	void deleteReferenceType(Long id) throws ResourceNotFoundException;

	void deleteBatchReferenceType(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertReferenceTypeListToMap(List<ReferenceType> referenceTypeReport);

}
