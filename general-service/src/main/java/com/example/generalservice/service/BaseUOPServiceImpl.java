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
	public List<BaseUOPResponse> getAllUop() {
		List<BaseUOP> uopList = baseUOPRepository.findAll();
		return uopList.stream().sorted(Comparator.comparing(BaseUOP::getId)).map(this::mapToBaseUOPResponse).toList();
	}

	@Override
	@Cacheable("uop")
	public BaseUOPResponse getUopById(Long id) throws ResourceNotFoundException {
		BaseUOP baseUOP = this.findUopById(id);
		return mapToBaseUOPResponse(baseUOP);
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
	public void deleteUopId(Long id) throws ResourceNotFoundException {
		BaseUOP baseUOP = this.findUopById(id);
		baseUOPRepository.deleteById(baseUOP.getId());
	}

	@Override
	public void deleteBatchUop(List<Long> ids) {
		baseUOPRepository.deleteAllByIdInBatch(ids);
	}

	private BaseUOPResponse mapToBaseUOPResponse(BaseUOP baseUOP) {
		return modelMapper.map(baseUOP, BaseUOPResponse.class);
	}

	private BaseUOP findUopById(Long id) throws ResourceNotFoundException {
		Optional<BaseUOP> uop = baseUOPRepository.findById(id);
		if (uop.isEmpty()) {
			throw new ResourceNotFoundException("No Uop found with this Id");
		}
		return uop.get();
	}

}
