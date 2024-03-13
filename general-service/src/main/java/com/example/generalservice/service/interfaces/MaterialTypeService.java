package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface MaterialTypeService {
	MaterialTypeResponse saveMaterial(MaterialTypeRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	MaterialTypeResponse getMaterialById(@NonNull Long id) throws ResourceNotFoundException;

	List<MaterialTypeResponse> getAllMaterial();

	List<MaterialTypeResponse> findAllStatusTrue();

	MaterialTypeResponse updateMaterial(@NonNull Long id, MaterialTypeRequest updateMaterialTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	MaterialTypeResponse updateMaterialStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<MaterialTypeResponse> updateBatchMaterialStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteMaterialId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchMaterial(List<Long> ids) throws ResourceNotFoundException;

}
