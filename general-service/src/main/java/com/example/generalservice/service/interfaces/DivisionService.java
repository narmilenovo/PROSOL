package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface DivisionService {
	DivisionResponse saveDivision(DivisionRequest divisionRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	DivisionResponse getDivisionById(@NonNull Long id) throws ResourceNotFoundException;

	List<DivisionResponse> getAllDivision();

	List<DivisionResponse> findAllStatusTrue();

	DivisionResponse updateDivision(@NonNull Long id, DivisionRequest updateDivisionRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	DivisionResponse updateDivisionStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<DivisionResponse> updateBatchDivisionStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteDivisionId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchDivision(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
