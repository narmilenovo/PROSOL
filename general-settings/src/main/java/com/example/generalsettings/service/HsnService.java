package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;

import jakarta.validation.Valid;

public interface HsnService {

	HsnResponse saveHsn(@Valid HsnRequest hsnRequest)throws ResourceNotFoundException, AlreadyExistsException;

	HsnResponse updateHsn(Long id, HsnRequest hsnRequest)throws ResourceNotFoundException, AlreadyExistsException;

	HsnResponse getHsnById(Long id)throws ResourceNotFoundException;

	HsnResponse updateStatusUsingHsnId(Long id)throws ResourceNotFoundException;

	List<HsnResponse> updateBulkStatusHsnId(List<Long> id);

	List<HsnResponse> getAllHsn();

	void deleteHsn(Long id)throws ResourceNotFoundException;

}
