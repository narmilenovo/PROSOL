package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;

import jakarta.validation.Valid;

public interface HsnService {

	HsnResponse saveHsn(@Valid HsnRequest hsnRequest) throws ResourceNotFoundException, AlreadyExistsException;

	HsnResponse getHsnById(Long id) throws ResourceNotFoundException;

	List<HsnResponse> getAllHsn();

	HsnResponse updateHsn(Long id, HsnRequest hsnRequest) throws ResourceNotFoundException, AlreadyExistsException;

	HsnResponse updateStatusUsingHsnId(Long id) throws ResourceNotFoundException;

	List<HsnResponse> updateBulkStatusHsnId(List<Long> id) throws ResourceNotFoundException;

	void deleteHsn(Long id) throws ResourceNotFoundException;

	void deleteBatchHsn(List<Long> ids) throws ResourceNotFoundException;

}
