package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface AccAssignmentService {
	AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<AccAssignmentResponse> getAllAcc();

	AccAssignmentResponse getAccById(Long id) throws ResourceNotFoundException;

	List<AccAssignmentResponse> findAllStatusTrue();

	AccAssignmentResponse updateAcc(Long id, AccAssignmentRequest updateAccAssignmentRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteAccId(Long id) throws ResourceNotFoundException;

	void deleteBatchAcc(List<Long> ids);
}
