package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface DivisionService {
	DivisionResponse saveDivision(DivisionRequest divisionRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	DivisionResponse getDivisionById(Long id) throws ResourceNotFoundException;

	List<DivisionResponse> getAllDivision();

	List<DivisionResponse> findAllStatusTrue();

	DivisionResponse updateDivision(Long id, DivisionRequest updateDivisionRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteDivisionId(Long id) throws ResourceNotFoundException;

	void deleteBatchDivsion(List<Long> ids);

}
