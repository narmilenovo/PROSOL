package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.MaterialType;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.MaterialTypeMapper;
import com.example.generalservice.repository.MaterialTypeRepository;
import com.example.generalservice.service.interfaces.MaterialTypeService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialTypeServiceImpl implements MaterialTypeService {
	private final MaterialTypeRepository materialTypeRepository;
	private final MaterialTypeMapper materialTypeMapper;
	private final DynamicClient dynamicClient;

	@Override
	public MaterialTypeResponse saveMaterial(MaterialTypeRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(alternateUOMRequest);
		String materialCode = alternateUOMRequest.getMaterialCode();
		String materialName = alternateUOMRequest.getMaterialName();
		if (materialTypeRepository.existsByMaterialCodeOrMaterialName(materialCode, materialName)) {
			throw new ResourceFoundException("Division Already Exist");
		}
		MaterialType materialType = materialTypeMapper.mapToMaterialType(alternateUOMRequest);
		validateDynamicFields(materialType);

		MaterialType savedType = materialTypeRepository.save(materialType);
		return materialTypeMapper.mapToMaterialTypeResponse(savedType);
	}

	@Override
	public MaterialTypeResponse getMaterialById(@NonNull Long id) throws ResourceNotFoundException {
		MaterialType materialType = this.findMaterialById(id);
		return materialTypeMapper.mapToMaterialTypeResponse(materialType);
	}

	@Override
	public List<MaterialTypeResponse> getAllMaterial() {
		return materialTypeRepository.findAll().stream().sorted(Comparator.comparing(MaterialType::getId))
				.map(materialTypeMapper::mapToMaterialTypeResponse).toList();
	}

	@Override
	public List<MaterialTypeResponse> findAllStatusTrue() {
		return materialTypeRepository.findAllByMaterialStatusIsTrue().stream()
				.sorted(Comparator.comparing(MaterialType::getId)).map(materialTypeMapper::mapToMaterialTypeResponse)
				.toList();
	}

	@Override
	public MaterialTypeResponse updateMaterial(@NonNull Long id, MaterialTypeRequest updateMaterialTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(updateMaterialTypeRequest);
		String materialCode = updateMaterialTypeRequest.getMaterialCode();
		String materialName = updateMaterialTypeRequest.getMaterialName();
		MaterialType existingMaterialType = this.findMaterialById(id);
		boolean exists = materialTypeRepository.existsByMaterialCodeAndIdNotOrMaterialNameAndIdNot(materialCode, id,
				materialName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingMaterialType.getMaterialCode().equals(materialCode)) {
				auditFields.add(
						new AuditFields(null, "Material Code", existingMaterialType.getMaterialCode(), materialCode));
				existingMaterialType.setMaterialCode(materialCode);
			}
			if (!existingMaterialType.getMaterialName().equals(materialName)) {
				auditFields.add(
						new AuditFields(null, "Material Name", existingMaterialType.getMaterialName(), materialName));
				existingMaterialType.setMaterialName(materialName);
			}
			if (!existingMaterialType.getMaterialStatus().equals(updateMaterialTypeRequest.getMaterialStatus())) {
				auditFields.add(new AuditFields(null, "Material Status", existingMaterialType.getMaterialStatus(),
						updateMaterialTypeRequest.getMaterialStatus()));
				existingMaterialType.setMaterialStatus(updateMaterialTypeRequest.getMaterialStatus());
			}
			if (!existingMaterialType.getDynamicFields().equals(updateMaterialTypeRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateMaterialTypeRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingMaterialType.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingMaterialType.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingMaterialType.updateAuditHistory(auditFields);
			MaterialType updatedMaterialType = materialTypeRepository.save(existingMaterialType);
			return materialTypeMapper.mapToMaterialTypeResponse(updatedMaterialType);
		}
		throw new ResourceFoundException("Material Already Exist");
	}

	@Override
	public MaterialTypeResponse updateMaterialStatus(@NonNull Long id) throws ResourceNotFoundException {
		MaterialType existingMaterialType = this.findMaterialById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingMaterialType.getMaterialStatus() != null) {
			auditFields.add(new AuditFields(null, "Material Status", existingMaterialType.getMaterialStatus(),
					!existingMaterialType.getMaterialStatus()));
			existingMaterialType.setMaterialStatus(!existingMaterialType.getMaterialStatus());
		}
		existingMaterialType.updateAuditHistory(auditFields);
		materialTypeRepository.save(existingMaterialType);
		return materialTypeMapper.mapToMaterialTypeResponse(existingMaterialType);
	}

	@Override
	public List<MaterialTypeResponse> updateBatchMaterialStatus(List<Long> ids) throws ResourceNotFoundException {
		List<MaterialType> materialTypes = this.findAllById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		materialTypes.forEach(existingMaterialType -> {
			if (existingMaterialType.getMaterialStatus() != null) {
				auditFields.add(new AuditFields(null, "Material Status", existingMaterialType.getMaterialStatus(),
						!existingMaterialType.getMaterialStatus()));
				existingMaterialType.setMaterialStatus(!existingMaterialType.getMaterialStatus());
			}
			existingMaterialType.updateAuditHistory(auditFields);
		});
		materialTypeRepository.saveAll(materialTypes);
		return materialTypes.stream().map(materialTypeMapper::mapToMaterialTypeResponse).toList();
	}

	@Override
	public void deleteMaterialId(@NonNull Long id) throws ResourceNotFoundException {
		MaterialType materialType = this.findMaterialById(id);
		if (materialType != null) {
			materialTypeRepository.delete(materialType);
		}
	}

	@Override
	public void deleteBatchMaterial(List<Long> ids) throws ResourceNotFoundException {
		List<MaterialType> materialTypes = this.findAllById(ids);
		if (!materialTypes.isEmpty()) {
			materialTypeRepository.deleteAll(materialTypes);
		}
	}

	private void validateDynamicFields(MaterialType materialType) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : materialType.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = MaterialType.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private MaterialType findMaterialById(@NonNull Long id) throws ResourceNotFoundException {
		return materialTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No material found with this Id"));
	}

	private List<MaterialType> findAllById(List<Long> ids) throws ResourceNotFoundException {

		Set<Long> idSet = new HashSet<>(ids);
		List<MaterialType> materialTypes = materialTypeRepository.findAllById(idSet);

		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Material Type with IDs " + missingIds + " not found");
		}

		return materialTypes;
	}

}
