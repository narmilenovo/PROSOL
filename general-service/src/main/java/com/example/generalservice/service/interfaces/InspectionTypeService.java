package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface InspectionTypeService {
	InspectionTypeResponse saveInType(InspectionTypeRequest inspectionTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	InspectionTypeResponse getInTypeById(@NonNull Long id) throws ResourceNotFoundException;

	List<InspectionTypeResponse> getAllInType();

	List<InspectionTypeResponse> findAllStatusTrue();

	InspectionTypeResponse updateInType(@NonNull Long id, InspectionTypeRequest updateInspectionTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	InspectionTypeResponse updateInTypeStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<InspectionTypeResponse> updateBatchInTypeStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteInTypeId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchInType(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
