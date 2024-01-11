package com.example.generalservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.entity.InspectionCode;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.InspectionCodeRepository;
import com.example.generalservice.service.interfaces.InspectionCodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InspectionCodeServiceImpl implements InspectionCodeService {
	private final InspectionCodeRepository inspectionCodeRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String inCode = inspectionCodeRequest.getInCodeCode();
		String inName = inspectionCodeRequest.getInCodeName();
		boolean exists = inspectionCodeRepository.existsByInCodeCodeOrInCodeName(inCode, inName);
		if (!exists) {
			InspectionCode inspectionCode = modelMapper.map(inspectionCodeRequest, InspectionCode.class);
			for (Map.Entry<String, Object> entryField : inspectionCode.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = InspectionCode.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			InspectionCode savedInCode = inspectionCodeRepository.save(inspectionCode);
			return mapToCodeResponse(savedInCode);
		} else {
			throw new ResourceFoundException("Inspection Code Already Exist");
		}
	}

	@Override
	@Cacheable("inCode")
	public List<InspectionCodeResponse> getAllInCode() {
		List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAll();
		return inspectionCodes.stream().sorted(Comparator.comparing(InspectionCode::getId)).map(this::mapToCodeResponse)
				.toList();
	}

	@Override
	@Cacheable("inCode")
	public InspectionCodeResponse getInCodeById(Long id) throws ResourceNotFoundException {
		InspectionCode inspectionCode = this.findInCodeById(id);
		return mapToCodeResponse(inspectionCode);
	}

	@Override
	@Cacheable("inCode")
	public List<InspectionCodeResponse> findAllStatusTrue() {
		List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAllByInCodeStatusIsTrue();
		return inspectionCodes.stream().sorted(Comparator.comparing(InspectionCode::getId)).map(this::mapToCodeResponse)
				.toList();
	}

	@Override
	public InspectionCodeResponse updateInCode(Long id, InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		String inCodeCode = updateInspectionCodeRequest.getInCodeCode();
		String inCodeName = updateInspectionCodeRequest.getInCodeName();
		InspectionCode existingInspectionCode = this.findInCodeById(id);
		boolean exists = inspectionCodeRepository.existsByInCodeCodeAndIdNotOrInCodeNameAndIdNot(inCodeCode, id,
				inCodeName, id);
		if (!exists) {
			modelMapper.map(updateInspectionCodeRequest, existingInspectionCode);
			for (Map.Entry<String, Object> entryField : existingInspectionCode.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = InspectionCode.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			InspectionCode updatedInspectionCode = inspectionCodeRepository.save(existingInspectionCode);
			return mapToCodeResponse(updatedInspectionCode);
		}
		throw new ResourceFoundException("Inspection code Already exist");
	}

	@Override
	public void deleteInCodeId(Long id) throws ResourceNotFoundException {
		InspectionCode inspectionCode = this.findInCodeById(id);
		inspectionCodeRepository.deleteById(inspectionCode.getId());
	}

	@Override
	public void deleteBatchInCode(List<Long> ids) {
		inspectionCodeRepository.deleteAllByIdInBatch(ids);
	}

	private InspectionCodeResponse mapToCodeResponse(InspectionCode inspectionCode) {
		return modelMapper.map(inspectionCode, InspectionCodeResponse.class);
	}

	private InspectionCode findInCodeById(Long id) throws ResourceNotFoundException {
		Optional<InspectionCode> inspectionCode = inspectionCodeRepository.findById(id);
		if (inspectionCode.isEmpty()) {
			throw new ResourceNotFoundException("No Inspection found with this Id");
		}
		return inspectionCode.get();
	}

}
