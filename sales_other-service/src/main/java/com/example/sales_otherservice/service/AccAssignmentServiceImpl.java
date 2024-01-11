package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DynamicClient;
import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.entity.AccAssignment;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.AccAssignmentRepository;
import com.example.sales_otherservice.service.interfaces.AccAssignmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccAssignmentServiceImpl implements AccAssignmentService {
	private final AccAssignmentRepository accAssignmentRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String accCode = accAssignmentRequest.getAccCode();
		String accName = accAssignmentRequest.getAccName();
		boolean exists = accAssignmentRepository.existsByAccCodeOrAccName(accCode, accName);
		if (!exists) {

			AccAssignment accAssignment = modelMapper.map(accAssignmentRequest, AccAssignment.class);
			for (Map.Entry<String, Object> entryField : accAssignment.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AccAssignment.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			AccAssignment savedAssignment = accAssignmentRepository.save(accAssignment);
			return mapToAccAssignmentResponse(savedAssignment);
		}
		throw new ResourceFoundException("Acc assignment already exist");
	}

	@Override
	public List<AccAssignmentResponse> getAllAcc() {
		List<AccAssignment> accAssignments = accAssignmentRepository.findAll();
		return accAssignments.stream().sorted(Comparator.comparing(AccAssignment::getId))
				.map(this::mapToAccAssignmentResponse).toList();
	}

	@Override
	public AccAssignmentResponse getAccById(Long id) throws ResourceNotFoundException {
		AccAssignment accAssignment = this.findAccById(id);
		return mapToAccAssignmentResponse(accAssignment);
	}

	@Override
	public List<AccAssignmentResponse> findAllStatusTrue() {
		List<AccAssignment> list = accAssignmentRepository.findAllByAccStatusIsTrue();
		return list.stream().sorted(Comparator.comparing(AccAssignment::getId)).map(this::mapToAccAssignmentResponse)
				.toList();
	}

	@Override
	public AccAssignmentResponse updateAcc(Long id, AccAssignmentRequest updateAccAssignmentRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		AccAssignment existingAssignment = this.findAccById(id);
		String accCode = updateAccAssignmentRequest.getAccCode();
		String accName = updateAccAssignmentRequest.getAccName();
		boolean exists = accAssignmentRepository.existsByAccCodeAndIdNotOrAccNameAndIdNot(accCode, id, accName, id);
		if (!exists) {
			modelMapper.map(updateAccAssignmentRequest, existingAssignment);
			for (Map.Entry<String, Object> entryField : existingAssignment.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AccAssignment.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			AccAssignment updatedAssignment = accAssignmentRepository.save(existingAssignment);
			return mapToAccAssignmentResponse(updatedAssignment);
		}
		throw new ResourceFoundException("Acc assignment already exist");
	}

	@Override
	public void deleteAccId(Long id) throws ResourceNotFoundException {
		AccAssignment accAssignment = this.findAccById(id);
		accAssignmentRepository.deleteById(accAssignment.getId());
	}

	@Override
	public void deleteBatchAcc(List<Long> ids) {
		accAssignmentRepository.deleteAllByIdInBatch(ids);
	}

	private AccAssignmentResponse mapToAccAssignmentResponse(AccAssignment accAssignment) {
		return modelMapper.map(accAssignment, AccAssignmentResponse.class);
	}

	private AccAssignment findAccById(Long id) throws ResourceNotFoundException {
		Optional<AccAssignment> accAssignment = accAssignmentRepository.findById(id);
		if (accAssignment.isEmpty()) {
			throw new ResourceNotFoundException("Account Assignment not found with this Id");
		}
		return accAssignment.get();
	}

}
