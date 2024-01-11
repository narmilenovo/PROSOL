package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface IndustrySectorService {
	IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<IndustrySectorResponse> getAllSector();

	IndustrySectorResponse getSectorById(Long id) throws ResourceNotFoundException;

	List<IndustrySectorResponse> findAllStatusTrue();

	IndustrySectorResponse updateSector(Long id, IndustrySectorRequest updateindustrysectorrequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteSectorId(Long id) throws ResourceNotFoundException;

	void deleteBatchSector(List<Long> ids);
}
