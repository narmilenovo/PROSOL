package com.example.generalservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.entity.InspectionType;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.InspectionTypeRepository;
import com.example.generalservice.service.interfaces.InspectionTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionTypeServiceImpl implements InspectionTypeService {
	private final InspectionTypeRepository inspectionTypeRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public InspectionTypeResponse saveInType(InspectionTypeRequest inspectionTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String inTypeCode = inspectionTypeRequest.getInTypeCode();
		String inTypeName = inspectionTypeRequest.getInTypeName();
		boolean exists = inspectionTypeRepository.existsByInTypeCodeOrInTypeName(inTypeCode, inTypeName);
		if (!exists) {

			InspectionType inspectionType = modelMapper.map(inspectionTypeRequest, InspectionType.class);
			for (Map.Entry<String, Object> entryField : inspectionType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = InspectionType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			InspectionType savedInspectionType = inspectionTypeRepository.save(inspectionType);
			return mapToInspectionTypeResponse(savedInspectionType);
		}
		throw new ResourceFoundException("Inspection type Already exist");

	}

	@Override
	@Cacheable("inType")
	public List<InspectionTypeResponse> getAllInType() {
		List<InspectionType> inspectionTypes = inspectionTypeRepository.findAll();
		return inspectionTypes.stream().sorted(Comparator.comparing(InspectionType::getId))
				.map(this::mapToInspectionTypeResponse).toList();
	}

	@Override
	@Cacheable("inType")
	public InspectionTypeResponse getInTypeById(Long id) throws ResourceNotFoundException {
		InspectionType inspectionType = this.findInTypeById(id);
		return mapToInspectionTypeResponse(inspectionType);
	}

	@Override
	@Cacheable("inType")
	public List<InspectionTypeResponse> findAllStatusTrue() {
		List<InspectionType> inspectionTypes = inspectionTypeRepository.findAllByInTypeStatusIsTrue();
		return inspectionTypes.stream().sorted(Comparator.comparing(InspectionType::getId))
				.map(this::mapToInspectionTypeResponse).toList();
	}

	@Override
	public InspectionTypeResponse updateInType(Long id, InspectionTypeRequest updateInspectionTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		String inTypeCode = updateInspectionTypeRequest.getInTypeCode();
		String inTypeName = updateInspectionTypeRequest.getInTypeName();
		InspectionType existingInspectionType = this.findInTypeById(id);
		boolean exists = inspectionTypeRepository.existsByInTypeCodeAndIdNotOrInTypeNameAndIdNot(inTypeCode, id,
				inTypeName, id);
		if (!exists) {
			modelMapper.map(updateInspectionTypeRequest, existingInspectionType);
			for (Map.Entry<String, Object> entryField : existingInspectionType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = InspectionType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			InspectionType updatedInspectionType = inspectionTypeRepository.save(existingInspectionType);
			return mapToInspectionTypeResponse(updatedInspectionType);
		}
		throw new ResourceFoundException("Inspection type Already exist");
	}

	@Override
	public void deleteInTypeId(Long id) throws ResourceNotFoundException {
		InspectionType inspectionType = this.findInTypeById(id);
		inspectionTypeRepository.deleteById(inspectionType.getId());
	}

	@Override
	public void deleteBatchInType(List<Long> ids) {
		inspectionTypeRepository.deleteAllById(ids);
	}

	private InspectionTypeResponse mapToInspectionTypeResponse(InspectionType inspectionType) {
		return modelMapper.map(inspectionType, InspectionTypeResponse.class);
	}

	private InspectionType findInTypeById(Long id) throws ResourceNotFoundException {
		Optional<InspectionType> inspectionType = inspectionTypeRepository.findById(id);
		if (inspectionType.isEmpty()) {
			throw new ResourceNotFoundException("No Inspection found with this Id");
		}
		return inspectionType.get();
	}

}
