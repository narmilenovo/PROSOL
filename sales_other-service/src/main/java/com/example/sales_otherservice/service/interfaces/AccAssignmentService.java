package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface AccAssignmentService {
	AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	AccAssignmentResponse getAccById(@NonNull Long id) throws ResourceNotFoundException;

	List<AccAssignmentResponse> getAllAcc();

	List<AccAssignmentResponse> findAllStatusTrue();

	AccAssignmentResponse updateAcc(@NonNull Long id, AccAssignmentRequest updateAccAssignmentRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	AccAssignmentResponse updateAccStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<AccAssignmentResponse> updateBatchAccStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteAccId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchAcc(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
