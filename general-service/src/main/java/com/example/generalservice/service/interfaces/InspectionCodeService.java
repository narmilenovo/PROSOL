package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface InspectionCodeService {
	InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<InspectionCodeResponse> getAllInCode();

	InspectionCodeResponse getInCodeById(Long id) throws ResourceNotFoundException;

	List<InspectionCodeResponse> findAllStatusTrue();

	InspectionCodeResponse updateInCode(Long id, InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteInCodeId(Long id) throws ResourceNotFoundException;

	void deleteBatchInCode(List<Long> ids);
}
