package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface UnitOfIssueService {
	UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	UnitOfIssueResponse getUOIById(Long id) throws ResourceNotFoundException;

	List<UnitOfIssueResponse> getAllUOI();

	List<UnitOfIssueResponse> findAllStatusTrue();

	UnitOfIssueResponse updateUOI(Long id, UnitOfIssueRequest updateUnitOfIssueRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	UnitOfIssueResponse updateUOIStatus(Long id) throws ResourceNotFoundException;

	List<UnitOfIssueResponse> updateBatchUOIStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteUOIId(Long id) throws ResourceNotFoundException;

	void deleteBatchUOI(List<Long> ids) throws ResourceNotFoundException;

}
