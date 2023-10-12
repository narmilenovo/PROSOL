package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface IndustrySectorService {
    IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest);

    List<IndustrySectorResponse> getAllSector();

    IndustrySectorResponse getSectorById(Long id) throws ResourceNotFoundException;

    List<IndustrySectorResponse> findAllStatusTrue();

    IndustrySectorResponse updateSector(Long id, IndustrySectorRequest updateindustrysectorrequest) throws ResourceNotFoundException;

    void deleteSectorId(Long id) throws ResourceNotFoundException;
}
