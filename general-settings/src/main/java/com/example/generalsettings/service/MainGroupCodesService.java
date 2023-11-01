package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;

import jakarta.validation.Valid;

public interface MainGroupCodesService {

	MainGroupCodesResponse saveMainGroupCodes(@Valid MainGroupCodesRequest mainGroupCodesRequest)throws ResourceNotFoundException, AlreadyExistsException;

	MainGroupCodesResponse updateMainGroupCodes(Long id, MainGroupCodesRequest mainGroupCodesRequest)throws ResourceNotFoundException, AlreadyExistsException;

	MainGroupCodesResponse getMainGroupCodesById(Long id)throws ResourceNotFoundException;

	void deleteMainGroupCodes(Long id)throws ResourceNotFoundException;

	MainGroupCodesResponse updateStatusUsingMainGroupCodesId(Long id)throws ResourceNotFoundException;

	List<MainGroupCodesResponse> updateBulkStatusMainGroupCodesId(List<Long> id);

	List<MainGroupCodesResponse> getAllMainGroupCodes();

}
