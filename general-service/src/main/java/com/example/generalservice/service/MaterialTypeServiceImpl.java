package com.example.generalservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.entity.MaterialType;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.MaterialTypeRepository;
import com.example.generalservice.service.interfaces.MaterialTypeService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialTypeServiceImpl implements MaterialTypeService {
	private final MaterialTypeRepository materialTypeRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public MaterialTypeResponse saveMaterial(MaterialTypeRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String materialCode = alternateUOMRequest.getMaterialCode();
		String materialName = alternateUOMRequest.getMaterialName();
		boolean exists = materialTypeRepository.existsByMaterialCodeOrMaterialName(materialCode, materialName);
		if (!exists) {
			MaterialType materialType = modelMapper.map(alternateUOMRequest, MaterialType.class);
			for (Map.Entry<String, Object> entryField : materialType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MaterialType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			MaterialType savedType = materialTypeRepository.save(materialType);
			return mapToMaterialTypeResponse(savedType);
		}
		throw new ResourceFoundException("Division Already Exist");
	}

	@Override
	@Cacheable("material")
	public MaterialTypeResponse getMaterialById(Long id) throws ResourceNotFoundException {
		MaterialType materialType = this.findMaterialById(id);
		return mapToMaterialTypeResponse(materialType);
	}

	@Override
	@Cacheable("material")
	public List<MaterialTypeResponse> getAllMaterial() {
		List<MaterialType> materialTypes = materialTypeRepository.findAll();
		return materialTypes.stream().sorted(Comparator.comparing(MaterialType::getId))
				.map(this::mapToMaterialTypeResponse).toList();
	}

	@Override
	@Cacheable("material")
	public List<MaterialTypeResponse> findAllStatusTrue() {
		List<MaterialType> materialTypes = materialTypeRepository.findAllByMaterialStatusIsTrue();
		return materialTypes.stream().sorted(Comparator.comparing(MaterialType::getId))
				.map(this::mapToMaterialTypeResponse).toList();
	}

	@Override
	public MaterialTypeResponse updateMaterial(Long id, MaterialTypeRequest updateMaterialTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.validateId(id);
		String materialCode = updateMaterialTypeRequest.getMaterialCode();
		String materialName = updateMaterialTypeRequest.getMaterialName();
		MaterialType existingMaterialType = this.findMaterialById(id);
		boolean exists = materialTypeRepository.existsByMaterialCodeAndIdNotOrMaterialNameAndIdNot(materialCode, id,
				materialName, id);
		if (!exists) {
			modelMapper.map(updateMaterialTypeRequest, existingMaterialType);
			for (Map.Entry<String, Object> entryField : existingMaterialType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MaterialType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			MaterialType updatedMaterialType = materialTypeRepository.save(existingMaterialType);
			return mapToMaterialTypeResponse(updatedMaterialType);
		}
		throw new ResourceFoundException("Material Already Exist");
	}

	@Override
	public MaterialTypeResponse updateMaterialStatus(Long id) throws ResourceNotFoundException {
		MaterialType existingMaterialType = this.findMaterialById(id);
		existingMaterialType.setMaterialStatus(!existingMaterialType.getMaterialStatus());
		materialTypeRepository.save(existingMaterialType);
		return mapToMaterialTypeResponse(existingMaterialType);
	}

	@Override
	public List<MaterialTypeResponse> updateBatchMaterialStatus(List<Long> ids) throws ResourceNotFoundException {
		List<MaterialType> materialTypes = this.findAllById(ids);
		materialTypes.forEach(materialType -> materialType.setMaterialStatus(!materialType.getMaterialStatus()));
		materialTypeRepository.saveAll(materialTypes);
		return materialTypes.stream().map(this::mapToMaterialTypeResponse).toList();
	}

	@Override
	public void deleteMaterialId(Long id) throws ResourceNotFoundException {
		MaterialType materialType = this.findMaterialById(id);
		materialTypeRepository.deleteById(materialType.getId());
	}

	@Override
	public void deleteBatchMaterial(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		materialTypeRepository.deleteAllByIdInBatch(ids);
	}

	private MaterialTypeResponse mapToMaterialTypeResponse(MaterialType materialType) {
		return modelMapper.map(materialType, MaterialTypeResponse.class);
	}

	private MaterialType findMaterialById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<MaterialType> materialType = materialTypeRepository.findById(id);
		if (materialType.isEmpty()) {
			throw new ResourceNotFoundException("No material found with this Id");
		}
		return materialType.get();
	}

	private List<MaterialType> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<MaterialType> materialTypes = materialTypeRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> materialTypes.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Material Type with IDs " + missingIds + " not found");
		}
		return materialTypes;
	}

}
