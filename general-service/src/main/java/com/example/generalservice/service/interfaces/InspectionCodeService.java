package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface InspectionCodeService {
	InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	InspectionCodeResponse getInCodeById(@NonNull Long id) throws ResourceNotFoundException;

	List<InspectionCodeResponse> getAllInCode();

	List<InspectionCodeResponse> findAllStatusTrue();

	InspectionCodeResponse updateInCode(@NonNull Long id, InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	InspectionCodeResponse updateInCodeStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<InspectionCodeResponse> updateBatchInCodeStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteInCodeId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchInCode(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
