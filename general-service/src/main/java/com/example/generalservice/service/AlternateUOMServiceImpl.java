package com.example.generalservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.entity.AlternateUOM;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.AlternateUOMRepository;
import com.example.generalservice.service.interfaces.AlternateUOMService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlternateUOMServiceImpl implements AlternateUOMService {
	private final AlternateUOMRepository alternateUOMRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String uomCode = alternateUOMRequest.getUomCode();
		String uomName = alternateUOMRequest.getUomName();
		boolean exists = alternateUOMRepository.existsByUomCodeOrUomName(uomCode, uomName);
		if (!exists) {
			AlternateUOM alternateUOM = modelMapper.map(alternateUOMRequest, AlternateUOM.class);
			for (Map.Entry<String, Object> entryField : alternateUOM.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AlternateUOM.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			AlternateUOM savedUom = alternateUOMRepository.save(alternateUOM);
			return mapToAlternateUOMResponse(savedUom);

		}
		throw new ResourceFoundException("Uom Already Exist");
	}

	@Override
	@Cacheable("uom")
	public List<AlternateUOMResponse> getAllUom() {
		List<AlternateUOM> uomList = alternateUOMRepository.findAll();
		return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId)).map(this::mapToAlternateUOMResponse)
				.toList();
	}

	@Override
	@Cacheable("uom")
	public AlternateUOMResponse getUomById(Long id) throws ResourceNotFoundException {
		AlternateUOM uom = this.findUomById(id);
		return mapToAlternateUOMResponse(uom);
	}

	@Override
	@Cacheable("uom")
	public List<AlternateUOMResponse> findAllStatusTrue() {
		List<AlternateUOM> uomList = alternateUOMRepository.findAllByUomStatusIsTrue();
		return uomList.stream().sorted(Comparator.comparing(AlternateUOM::getId)).map(this::mapToAlternateUOMResponse)
				.toList();
	}

	@Override
	public AlternateUOMResponse updateUom(Long id, AlternateUOMRequest updateAlternateUOMRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		String uomCode = updateAlternateUOMRequest.getUomCode();
		String uomName = updateAlternateUOMRequest.getUomName();
		AlternateUOM existingUom = this.findUomById(id);
		boolean exists = alternateUOMRepository.existsByUomCodeAndIdNotOrUomNameAndIdNot(uomCode, id, uomName, id);
		if (!exists) {
			modelMapper.map(updateAlternateUOMRequest, existingUom);
			for (Map.Entry<String, Object> entryField : existingUom.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AlternateUOM.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			AlternateUOM updatedUom = alternateUOMRepository.save(existingUom);
			return mapToAlternateUOMResponse(updatedUom);
		}
		throw new ResourceFoundException("Uom Already Exist");
	}

	@Override
	public void deleteUomId(Long id) throws ResourceNotFoundException {
		AlternateUOM uom = this.findUomById(id);
		alternateUOMRepository.deleteById(uom.getId());
	}

	@Override
	public void deleteBatchUom(List<Long> ids) {
		alternateUOMRepository.deleteAllByIdInBatch(ids);

	}

	private AlternateUOMResponse mapToAlternateUOMResponse(AlternateUOM alternateUOM) {
		return modelMapper.map(alternateUOM, AlternateUOMResponse.class);
	}

	private AlternateUOM findUomById(Long id) throws ResourceNotFoundException {
		Optional<AlternateUOM> uom = alternateUOMRepository.findById(id);
		if (uom.isEmpty()) {
			throw new ResourceNotFoundException("No Uom found with this");
		}
		return uom.get();
	}

}
