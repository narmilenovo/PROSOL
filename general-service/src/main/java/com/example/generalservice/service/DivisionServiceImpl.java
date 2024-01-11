package com.example.generalservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.entity.Division;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.DivisionRepository;
import com.example.generalservice.service.interfaces.DivisionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {
	private final DivisionRepository divisionRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DivisionResponse saveDivision(DivisionRequest divisionRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String divCode = divisionRequest.getDivCode();
		String divName = divisionRequest.getDivName();
		boolean exists = divisionRepository.existsByDivCodeOrDivName(divCode, divName);
		if (!exists) {

			Division division = modelMapper.map(divisionRequest, Division.class);
			for (Map.Entry<String, Object> entryField : division.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Division.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			Division savedDivision = divisionRepository.save(division);
			return mapToDivisionResponse(savedDivision);
		}
		throw new ResourceFoundException("Division Already Exist");
	}

	@Override
	@Cacheable("div")
	public DivisionResponse getDivisionById(Long id) throws ResourceNotFoundException {
		Division division = this.findDivisionById(id);
		return mapToDivisionResponse(division);
	}

	@Override
	@Cacheable("div")
	public List<DivisionResponse> getAllDivision() {
		List<Division> divisionList = divisionRepository.findAll();
		return divisionList.stream().sorted(Comparator.comparing(Division::getId)).map(this::mapToDivisionResponse)
				.toList();
	}

	@Override
	@Cacheable("div")
	public List<DivisionResponse> findAllStatusTrue() {
		List<Division> divisionList = divisionRepository.findAllByDivStatusIsTrue();
		return divisionList.stream().sorted(Comparator.comparing(Division::getId)).map(this::mapToDivisionResponse)
				.toList();
	}

	@Override
	public DivisionResponse updateDivision(Long id, DivisionRequest updateDivisionRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		String divCode = updateDivisionRequest.getDivCode();
		String divName = updateDivisionRequest.getDivName();
		Division existingDivision = this.findDivisionById(id);
		boolean exists = divisionRepository.existsByDivCodeAndIdNotOrDivNameAndIdNot(divCode, id, divName, id);
		if (!exists) {
			modelMapper.map(updateDivisionRequest, existingDivision);
			for (Map.Entry<String, Object> entryField : existingDivision.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = Division.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			Division updatedDivision = divisionRepository.save(existingDivision);
			return mapToDivisionResponse(updatedDivision);
		}
		throw new ResourceFoundException("Division Already Exist");
	}

	@Override
	public void deleteDivisionId(Long id) throws ResourceNotFoundException {
		Division division = this.findDivisionById(id);
		divisionRepository.deleteById(division.getId());

	}

	@Override
	public void deleteBatchDivsion(List<Long> ids) {
		divisionRepository.deleteAllByIdInBatch(ids);
	}

	private DivisionResponse mapToDivisionResponse(Division division) {
		return modelMapper.map(division, DivisionResponse.class);
	}

	private Division findDivisionById(Long id) throws ResourceNotFoundException {
		Optional<Division> division = divisionRepository.findById(id);
		if (division.isEmpty()) {
			throw new ResourceNotFoundException("No Division found with this Id");
		}
		return division.get();
	}

}
