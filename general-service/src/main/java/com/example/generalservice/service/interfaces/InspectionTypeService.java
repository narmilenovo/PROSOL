package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface InspectionTypeService {
	InspectionTypeResponse saveInType(InspectionTypeRequest inspectionTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	InspectionTypeResponse getInTypeById(Long id) throws ResourceNotFoundException;

	List<InspectionTypeResponse> getAllInType();

	List<InspectionTypeResponse> findAllStatusTrue();

	InspectionTypeResponse updateInType(Long id, InspectionTypeRequest updateInspectionTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	InspectionTypeResponse updateInTypeStatus(Long id) throws ResourceNotFoundException;

	List<InspectionTypeResponse> updateBatchInTypeStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteInTypeId(Long id) throws ResourceNotFoundException;

	void deleteBatchInType(List<Long> ids) throws ResourceNotFoundException;

}
