package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface BaseUOPService {
	BaseUOPResponse saveUop(BaseUOPRequest baseUOPRequest) throws ResourceFoundException, ResourceNotFoundException;

	BaseUOPResponse getUopById(Long id) throws ResourceNotFoundException;

	List<BaseUOPResponse> getAllUop();

	List<BaseUOPResponse> findAllStatusTrue();

	BaseUOPResponse updateUop(Long id, BaseUOPRequest updateBaseUOPRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	BaseUOPResponse updateUopStatus(Long id) throws ResourceNotFoundException;

	List<BaseUOPResponse> updateBatchUopStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteUopId(Long id) throws ResourceNotFoundException;

	void deleteBatchUop(List<Long> ids) throws ResourceNotFoundException;

}
