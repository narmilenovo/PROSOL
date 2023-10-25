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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UnitOfIssueServiceImpl implements UnitOfIssueService {
    private final UnitOfIssueRepository unitOfIssueRepository;
    private final ModelMapper modelMapper;

    @Override
    public UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest) throws ResourceFoundException {
        String uoiCode = unitOfIssueRequest.getUoiCode();
        String uoiName = unitOfIssueRequest.getUoiName();
        boolean exists = unitOfIssueRepository.existsByUoiCodeOrUoiName(uoiCode, uoiName);
        if (!exists) {

            UnitOfIssue unitOfIssue = modelMapper.map(unitOfIssueRequest, UnitOfIssue.class);
            UnitOfIssue savedUnitOfIssue = unitOfIssueRepository.save(unitOfIssue);
            return mapToUnitOfIssueResponse(savedUnitOfIssue);
        }
        throw new ResourceFoundException("Unit Of Issue is already exist");
    }

    @Override
    @Cacheable("uoi")
    public List<UnitOfIssueResponse> getAllUOI() {
        List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAll();
        return unitOfIssues.stream()
                .sorted(Comparator.comparing(UnitOfIssue::getId))
                .map(this::mapToUnitOfIssueResponse)
                .toList();
    }

    @Override
    @Cacheable("uoi")
    public UnitOfIssueResponse getUOIById(Long id) throws ResourceNotFoundException {
        UnitOfIssue unitOfIssue = this.findUOIById(id);
        return mapToUnitOfIssueResponse(unitOfIssue);
    }

    @Override
    @Cacheable("uoi")
    public List<UnitOfIssueResponse> findAllStatusTrue() {
        List<UnitOfIssue> unitOfIssues = unitOfIssueRepository.findAllByUoiStatusIsTrue();
        return unitOfIssues.stream()
                .sorted(Comparator.comparing(UnitOfIssue::getId))
                .map(this::mapToUnitOfIssueResponse)
                .toList();
    }

    @Override
    public UnitOfIssueResponse updateUOI(Long id, UnitOfIssueRequest updateUnitOfIssueRequest) throws ResourceNotFoundException, ResourceFoundException {
        String uoiCode = updateUnitOfIssueRequest.getUoiCode();
        String uoiName = updateUnitOfIssueRequest.getUoiName();
        UnitOfIssue existingUnitOfIssue = this.findUOIById(id);
        boolean exists = unitOfIssueRepository.existsByUoiCodeAndIdNotOrUoiNameAndIdNot(uoiCode, id, uoiName, id);
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
