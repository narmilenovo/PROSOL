package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface InspectionCodeService {
	InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	InspectionCodeResponse getInCodeById(Long id) throws ResourceNotFoundException;

	List<InspectionCodeResponse> getAllInCode();

	List<InspectionCodeResponse> findAllStatusTrue();

	InspectionCodeResponse updateInCode(Long id, InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	InspectionCodeResponse updateInCodeStatus(Long id) throws ResourceNotFoundException;

	List<InspectionCodeResponse> updateBatchInCodeStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteInCodeId(Long id) throws ResourceNotFoundException;

	void deleteBatchInCode(List<Long> ids) throws ResourceNotFoundException;

}
