package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface IndustrySectorService {
	IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	IndustrySectorResponse getSectorById(@NonNull Long id) throws ResourceNotFoundException;

	List<IndustrySectorResponse> getAllSector();

	List<IndustrySectorResponse> findAllStatusTrue();

	IndustrySectorResponse updateSector(@NonNull Long id, IndustrySectorRequest updateindustrysectorrequest)
			throws ResourceNotFoundException, ResourceFoundException;

	IndustrySectorResponse updateSectorStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<IndustrySectorResponse> updateBatchSectorResponseStatus(@NonNull List<Long> ids);

	void deleteSectorId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchSector(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
