package com.example.generalsettings.service;

import java.util.List;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.NmUomRequest;
import com.example.generalsettings.response.NmUomResponse;

import jakarta.validation.Valid;

public interface NmUomService {

	List<NmUomResponse> getAllNmUom();

	List<NmUomResponse> updateBulkStatusNmUomId(List<Long> id);

	NmUomResponse updateStatusUsingNmUomId(Long id)throws ResourceNotFoundException ;

	void deleteNmUom(Long id)throws ResourceNotFoundException ;

	NmUomResponse getNmUomById(Long id)throws ResourceNotFoundException ;

	NmUomResponse updateNmUom(Long id, NmUomRequest nmUomRequest)throws ResourceNotFoundException, AlreadyExistsException;

	NmUomResponse saveNmUom(@Valid NmUomRequest nmUomRequest)throws ResourceNotFoundException, AlreadyExistsException;

}
