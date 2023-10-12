package com.example.generalservice.service;

import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.entity.UnitOfIssue;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.UnitOfIssueRepository;
import com.example.generalservice.service.interfaces.UnitOfIssueService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnitOfIssueServiceImpl implements UnitOfIssueService {
    private final UnitOfIssueRepository unitOfIssueRepository;
    private final ModelMapper modelMapper;

    @Override
    public UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest) {
        UnitOfIssue unitOfIssue = modelMapper.map(unitOfIssueRequest, UnitOfIssue.class);
        UnitOfIssue savedUnitOfIssue = unitOfIssueRepository.save(unitOfIssue);
        return mapToUnitOfIssueResponse(savedUnitOfIssue);
    }

    @Override
    public List<UnitOfIssueResponse> getAllUOI() {
        List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAll();
        return unitOfIssues.stream().map(this::mapToUnitOfIssueResponse).toList();
    }

    @Override
    public UnitOfIssueResponse getUOIById(Long id) throws ResourceNotFoundException {
        UnitOfIssue unitOfIssue = this.findUOIById(id);
        return mapToUnitOfIssueResponse(unitOfIssue);
    }

    @Override
    public List<UnitOfIssueResponse> findAllStatusTrue() {
        List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAllByUoiStatusIsTrue();
        return unitOfIssues.stream().map(this::mapToUnitOfIssueResponse).toList();
    }

    @Override
    public UnitOfIssueResponse updateUOI(Long id, UnitOfIssueRequest updateUnitOfIssueRequest) throws ResourceNotFoundException, ResourceFoundException {
        String uoiCode = updateUnitOfIssueRequest.getUoiCode();
        UnitOfIssue existingUnitOfIssue = this.findUOIById(id);
        boolean exists = unitOfIssueRepository.existsByUoiCode(uoiCode);
        if (!exists) {
            modelMapper.map(updateUnitOfIssueRequest, existingUnitOfIssue);
            UnitOfIssue updatedUnitOfIssue = unitOfIssueRepository.save(existingUnitOfIssue);
            return mapToUnitOfIssueResponse(updatedUnitOfIssue);
        }
        throw new ResourceFoundException("Unit Of Issue is already exist");
    }

    @Override
    public void deleteUOIId(Long id) throws ResourceNotFoundException {
        UnitOfIssue unitOfIssue = this.findUOIById(id);
        unitOfIssueRepository.deleteById(unitOfIssue.getId());
    }

    private UnitOfIssueResponse mapToUnitOfIssueResponse(UnitOfIssue unitOfIssue) {
        return modelMapper.map(unitOfIssue, UnitOfIssueResponse.class);
    }

    private UnitOfIssue findUOIById(Long id) throws ResourceNotFoundException {
        Optional<UnitOfIssue> unitOfIssue = unitOfIssueRepository.findById(id);
        if (unitOfIssue.isEmpty()) {
            throw new ResourceNotFoundException("No Unit Of Issue found with this Id");
        }
        return unitOfIssue.get();
    }
}
