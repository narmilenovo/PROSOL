package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface MaterialTypeService {
    MaterialTypeResponse saveMaterial(MaterialTypeRequest alternateUOMRequest);

    List<MaterialTypeResponse> getAllMaterial();

    MaterialTypeResponse getMaterialById(Long id) throws ResourceNotFoundException;

    List<MaterialTypeResponse> findAllStatusTrue();

    MaterialTypeResponse updateMaterial(Long id, MaterialTypeRequest updateMaterialTypeRequest) throws ResourceNotFoundException;

    void deleteMaterialId(Long id) throws ResourceNotFoundException;
}
