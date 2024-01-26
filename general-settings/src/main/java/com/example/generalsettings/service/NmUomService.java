package com.example.generalsettings.service;

import java.util.List;
import java.util.Map;

import com.example.generalsettings.entity.NmUom;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.NmUomRequest;
import com.example.generalsettings.response.NmUomResponse;

import jakarta.validation.Valid;

public interface NmUomService {
	NmUomResponse saveNmUom(@Valid NmUomRequest nmUomRequest) throws ResourceNotFoundException, AlreadyExistsException;

	NmUomResponse getNmUomById(Long id) throws ResourceNotFoundException;

	List<NmUom> findAll();

	List<NmUomResponse> getAllNmUom();

	NmUomResponse updateNmUom(Long id, NmUomRequest nmUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException;

	List<NmUomResponse> updateBulkStatusNmUomId(List<Long> id) throws ResourceNotFoundException;

	NmUomResponse updateStatusUsingNmUomId(Long id) throws ResourceNotFoundException;

	void deleteNmUom(Long id) throws ResourceNotFoundException;

	void deleteBatchNmUom(List<Long> ids) throws ResourceNotFoundException;

	List<Map<String, Object>> convertNmUomListToMap(List<NmUom> nmUomReport);
}
