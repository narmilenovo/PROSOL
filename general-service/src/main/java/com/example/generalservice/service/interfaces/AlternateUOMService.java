package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface AlternateUOMService {
	AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	AlternateUOMResponse getUomById(@NonNull Long id) throws ResourceNotFoundException;

	List<AlternateUOMResponse> getAllUom() throws ResourceNotFoundException;

	List<AlternateUOMResponse> findAllStatusTrue() throws ResourceNotFoundException;

	AlternateUOMResponse updateUom(@NonNull Long id, AlternateUOMRequest updateAlternateUOMRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteUomId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchUom(@NonNull List<Long> ids) throws ResourceNotFoundException;

	AlternateUOMResponse updateUomStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<AlternateUOMResponse> updateBatchUomStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;
}
