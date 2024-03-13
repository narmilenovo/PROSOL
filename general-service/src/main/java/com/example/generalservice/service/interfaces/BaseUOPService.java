package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface BaseUOPService {
	BaseUOPResponse saveUop(BaseUOPRequest baseUOPRequest) throws ResourceFoundException, ResourceNotFoundException;

	BaseUOPResponse getUopById(@NonNull Long id) throws ResourceNotFoundException;

	List<BaseUOPResponse> getAllUop();

	List<BaseUOPResponse> findAllStatusTrue();

	BaseUOPResponse updateUop(@NonNull Long id, BaseUOPRequest updateBaseUOPRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	BaseUOPResponse updateUopStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<BaseUOPResponse> updateBatchUopStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteUopId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchUop(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
