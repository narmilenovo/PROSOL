package com.example.generalservice.service;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.entity.InspectionCode;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.InspectionCodeRepository;
import com.example.generalservice.service.interfaces.InspectionCodeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InspectionCodeServiceImpl implements InspectionCodeService {
    private final InspectionCodeRepository inspectionCodeRepository;
    private final ModelMapper modelMapper;

    @Override
    public InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest) throws ResourceFoundException {
        String inCode = inspectionCodeRequest.getInCodeCode();
        String inName = inspectionCodeRequest.getInCodeName();
        boolean exists = inspectionCodeRepository.existsByInCodeCodeOrInCodeName(inCode, inName);
        if (!exists) {
            InspectionCode inspectionCode = modelMapper.map(inspectionCodeRequest, InspectionCode.class);
            InspectionCode savedInCode = inspectionCodeRepository.save(inspectionCode);
            return mapToCodeResponse(savedInCode);
        } else {
            throw new ResourceFoundException("Inspection Code Already Exist");
        }
    }

    @Override
    public List<InspectionCodeResponse> getAllInCode() {
        List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAll();
        return inspectionCodes.stream().map(this::mapToCodeResponse).toList();
    }

    @Override
    public InspectionCodeResponse getInCodeById(Long id) throws ResourceNotFoundException {
        InspectionCode inspectionCode = this.findInCodeById(id);
        return mapToCodeResponse(inspectionCode);
    }

    @Override
    public List<InspectionCodeResponse> findAllStatusTrue() {
        List<InspectionCode> inspectionCodes = inspectionCodeRepository.findAllByInCodeStatusIsTrue();
        return inspectionCodes.stream().map(this::mapToCodeResponse).toList();
    }

    @Override
    public InspectionCodeResponse updateInCode(Long id, InspectionCodeRequest updateInspectionCodeRequest) throws ResourceNotFoundException, ResourceFoundException {
        String inCodeCode = updateInspectionCodeRequest.getInCodeCode();
        String inCodeName = updateInspectionCodeRequest.getInCodeName();
        InspectionCode existingInspectionCode = this.findInCodeById(id);
        boolean exists = inspectionCodeRepository.existsByInCodeCodeAndIdNotOrInCodeNameAndIdNot(inCodeCode, id, inCodeName, id);
        if (!exists) {
            modelMapper.map(updateInspectionCodeRequest, existingInspectionCode);
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
