package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface IndustrySectorService {
	IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	IndustrySectorResponse getSectorById(Long id) throws ResourceNotFoundException;

	List<IndustrySectorResponse> getAllSector();

	List<IndustrySectorResponse> findAllStatusTrue();

	IndustrySectorResponse updateSector(Long id, IndustrySectorRequest updateindustrysectorrequest)
			throws ResourceNotFoundException, ResourceFoundException;

	IndustrySectorResponse updateSectorStatus(Long id) throws ResourceNotFoundException;

	List<IndustrySectorResponse> updateBatchSectorResponseStatus(List<Long> ids);

	void deleteSectorId(Long id) throws ResourceNotFoundException;

	void deleteBatchSector(List<Long> ids) throws ResourceNotFoundException;

}
