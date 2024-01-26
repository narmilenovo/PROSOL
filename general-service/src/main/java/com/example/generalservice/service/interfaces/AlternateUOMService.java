package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface AlternateUOMService {
	AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	AlternateUOMResponse getUomById(Long id) throws ResourceNotFoundException;

	List<AlternateUOMResponse> getAllUom() throws ResourceNotFoundException;

	List<AlternateUOMResponse> findAllStatusTrue() throws ResourceNotFoundException;

	AlternateUOMResponse updateUom(Long id, AlternateUOMRequest updateAlternateUOMRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteUomId(Long id) throws ResourceNotFoundException;

	void deleteBatchUom(List<Long> ids) throws ResourceNotFoundException;

	AlternateUOMResponse updateUomStatus(Long id) throws ResourceNotFoundException;

	List<AlternateUOMResponse> updateBatchUomStatus(List<Long> ids) throws ResourceNotFoundException;
}
