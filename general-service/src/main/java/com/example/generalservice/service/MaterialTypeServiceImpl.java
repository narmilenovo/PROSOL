package com.example.generalservice.service;

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.entity.MaterialType;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.MaterialTypeRepository;
import com.example.generalservice.service.interfaces.MaterialTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialTypeServiceImpl implements MaterialTypeService {
    private final MaterialTypeRepository materialTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public MaterialTypeResponse saveMaterial(MaterialTypeRequest alternateUOMRequest) throws ResourceFoundException {
        String materialCode = alternateUOMRequest.getMaterialCode();
        String materialName = alternateUOMRequest.getMaterialName();
        boolean exists = materialTypeRepository.existsByMaterialCodeOrMaterialName(materialCode, materialName);
        if (!exists) {

            MaterialType materialType = modelMapper.map(alternateUOMRequest, MaterialType.class);
            MaterialType savedType = materialTypeRepository.save(materialType);
            return mapToMaterialTypeResponse(savedType);
        }
        throw new ResourceFoundException("Division Already Exist");
    }

    @Override
    @Cacheable("material")
    public List<MaterialTypeResponse> getAllMaterial() {
        List<MaterialType> materialTypes = materialTypeRepository.findAll();
        return materialTypes.stream()
                .sorted(Comparator.comparing(MaterialType::getId))
                .map(this::mapToMaterialTypeResponse)
                .toList();
    }

    @Override
    @Cacheable("material")
    public MaterialTypeResponse getMaterialById(Long id) throws ResourceNotFoundException {
        MaterialType materialType = this.findMaterialById(id);
        return mapToMaterialTypeResponse(materialType);
    }

    @Override
    @Cacheable("material")
    public List<MaterialTypeResponse> findAllStatusTrue() {
        List<MaterialType> materialTypes = materialTypeRepository.findAllByMaterialStatusIsTrue();
        return materialTypes.stream()
                .sorted(Comparator.comparing(MaterialType::getId))
                .map(this::mapToMaterialTypeResponse)
                .toList();
    }

    @Override
    public MaterialTypeResponse updateMaterial(Long id, MaterialTypeRequest updateMaterialTypeRequest) throws ResourceFoundException, ResourceNotFoundException {
        String materialCode = updateMaterialTypeRequest.getMaterialCode();
        String materialName = updateMaterialTypeRequest.getMaterialName();
        MaterialType existingMaterialType = this.findMaterialById(id);
        boolean exists = materialTypeRepository.existsByMaterialCodeAndIdNotOrMaterialNameAndIdNot(materialCode, id, materialName, id);
        if (!exists) {
            modelMapper.map(updateMaterialTypeRequest, existingMaterialType);
            MaterialType updatedMaterialType = materialTypeRepository.save(existingMaterialType);
            return mapToMaterialTypeResponse(updatedMaterialType);
        }
        throw new ResourceFoundException("Material Already Exist");
    }

    @Override
    public void deleteMaterialId(Long id) throws ResourceNotFoundException {
        MaterialType materialType = this.findMaterialById(id);
        materialTypeRepository.deleteById(materialType.getId());
    }

    private MaterialTypeResponse mapToMaterialTypeResponse(MaterialType materialType) {
        return modelMapper.map(materialType, MaterialTypeResponse.class);
    }

    private MaterialType findMaterialById(Long id) throws ResourceNotFoundException {
        Optional<MaterialType> materialType = materialTypeRepository.findById(id);
        if (materialType.isEmpty()) {
            throw new ResourceNotFoundException("No material found with this Id");
        }
        return materialType.get();
    }

}