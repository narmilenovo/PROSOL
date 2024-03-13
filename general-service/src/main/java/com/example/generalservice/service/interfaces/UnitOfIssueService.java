package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface UnitOfIssueService {
	UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	UnitOfIssueResponse getUOIById(@NonNull Long id) throws ResourceNotFoundException;

	List<UnitOfIssueResponse> getAllUOI();

	List<UnitOfIssueResponse> findAllStatusTrue();

	UnitOfIssueResponse updateUOI(@NonNull Long id, UnitOfIssueRequest updateUnitOfIssueRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	UnitOfIssueResponse updateUOIStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<UnitOfIssueResponse> updateBatchUOIStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteUOIId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchUOI(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
