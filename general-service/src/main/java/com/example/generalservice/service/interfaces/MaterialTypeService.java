package com.example.generalservice.service.interfaces;

import java.util.List;

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface MaterialTypeService {
	MaterialTypeResponse saveMaterial(MaterialTypeRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	MaterialTypeResponse getMaterialById(Long id) throws ResourceNotFoundException;

	List<MaterialTypeResponse> getAllMaterial();

	List<MaterialTypeResponse> findAllStatusTrue();

	MaterialTypeResponse updateMaterial(Long id, MaterialTypeRequest updateMaterialTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	MaterialTypeResponse updateMaterialStatus(Long id) throws ResourceNotFoundException;

	List<MaterialTypeResponse> updateBatchMaterialStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteMaterialId(Long id) throws ResourceNotFoundException;

	void deleteBatchMaterial(List<Long> ids) throws ResourceNotFoundException;

}
