package com.example.generalservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.entity.BaseUOP;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.BaseUOPRepository;
import com.example.generalservice.service.interfaces.BaseUOPService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BaseUOPServiceImpl implements BaseUOPService {
	private final BaseUOPRepository baseUOPRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public BaseUOPResponse saveUop(BaseUOPRequest baseUOPRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String uopCode = baseUOPRequest.getUopCode();
		String uopName = baseUOPRequest.getUopName();
		boolean exists = baseUOPRepository.existsByUopCodeOrUopName(uopCode, uopName);
		if (!exists) {
			BaseUOP baseUOP = modelMapper.map(baseUOPRequest, BaseUOP.class);
			for (Map.Entry<String, Object> entryField : baseUOP.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = BaseUOP.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			BaseUOP savedUop = baseUOPRepository.save(baseUOP);
			return mapToBaseUOPResponse(savedUop);
		}
		throw new ResourceFoundException("Uop Already Exists");
	}

	@Override
	@Cacheable("uop")
	public BaseUOPResponse getUopById(Long id) throws ResourceNotFoundException {
		BaseUOP baseUOP = this.findUopById(id);
		return mapToBaseUOPResponse(baseUOP);
	}

	@Override
	@Cacheable("uop")
	public List<BaseUOPResponse> getAllUop() {
		List<BaseUOP> uopList = baseUOPRepository.findAll();
		return uopList.stream().sorted(Comparator.comparing(BaseUOP::getId)).map(this::mapToBaseUOPResponse).toList();
	}

	@Override
	@Cacheable("uop")
	public List<BaseUOPResponse> findAllStatusTrue() {
		List<BaseUOP> uopList = baseUOPRepository.findAllByUopStatusIsTrue();
		return uopList.stream().sorted(Comparator.comparing(BaseUOP::getId)).map(this::mapToBaseUOPResponse).toList();
	}

	@Override
	public BaseUOPResponse updateUop(Long id, BaseUOPRequest updateBaseUOPRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String uopCode = updateBaseUOPRequest.getUopCode();
		String uopName = updateBaseUOPRequest.getUopName();
		BaseUOP existingBaseUOP = this.findUopById(id);
		boolean exists = baseUOPRepository.existsByUopCodeAndIdNotOrUopNameAndIdNot(uopCode, id, uopName, id);
		if (!exists) {
			modelMapper.map(updateBaseUOPRequest, existingBaseUOP);
			BaseUOP updatedBaseUOP = baseUOPRepository.save(existingBaseUOP);
			return mapToBaseUOPResponse(updatedBaseUOP);
		}
		throw new ResourceFoundException("Uop Already Exist");
	}

	@Override
	public BaseUOPResponse updateUopStatus(Long id) throws ResourceNotFoundException {
		BaseUOP existingBaseUOP = this.findUopById(id);
		existingBaseUOP.setUopStatus(!existingBaseUOP.getUopStatus());
		baseUOPRepository.save(existingBaseUOP);
		return this.mapToBaseUOPResponse(existingBaseUOP);
	}

	@Override
	public List<BaseUOPResponse> updateBatchUopStatus(List<Long> ids) throws ResourceNotFoundException {
		List<BaseUOP> baseUOPs = this.findAllById(ids);
		baseUOPs.forEach(baseUOP -> baseUOP.setUopStatus(!baseUOP.getUopStatus()));
		baseUOPRepository.saveAll(baseUOPs);
		return baseUOPs.stream().map(this::mapToBaseUOPResponse).toList();
	}

	@Override
	public void deleteUopId(Long id) throws ResourceNotFoundException {
		BaseUOP baseUOP = this.findUopById(id);
		baseUOPRepository.deleteById(baseUOP.getId());
	}

	@Override
	public void deleteBatchUop(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		baseUOPRepository.deleteAllByIdInBatch(ids);
	}

	private BaseUOPResponse mapToBaseUOPResponse(BaseUOP baseUOP) {
		return modelMapper.map(baseUOP, BaseUOPResponse.class);
	}

	private BaseUOP findUopById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<BaseUOP> uop = baseUOPRepository.findById(id);
		if (uop.isEmpty()) {
			throw new ResourceNotFoundException("No Uop found with this Id");
		}
		return uop.get();
	}

	private List<BaseUOP> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<BaseUOP> baseUOPs = baseUOPRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> baseUOPs.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Alternate Uom with IDs " + missingIds + " not found.");
		}
		return baseUOPs;
	}

}
