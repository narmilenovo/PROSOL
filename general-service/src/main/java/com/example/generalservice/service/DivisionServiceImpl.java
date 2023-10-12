package com.example.generalservice.service;

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.entity.Division;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.DivisionRepository;
import com.example.generalservice.service.interfaces.DivisionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DivisionServiceImpl implements DivisionService {
    private final DivisionRepository divisionRepository;
    private final ModelMapper modelMapper;


    @Override
    public DivisionResponse saveDivision(DivisionRequest divisionRequest) {
        Division division = modelMapper.map(divisionRequest, Division.class);
        Division savedDivision = divisionRepository.save(division);
        return mapToDivisionResponse(savedDivision);
    }

    @Override
    public DivisionResponse getDivisionById(Long id) throws ResourceNotFoundException {
        Division division = this.findDivisionById(id);
        return mapToDivisionResponse(division);
    }

    @Override
    public List<DivisionResponse> getAllDivision() {
        List<Division> divisionList = divisionRepository.findAll();
        return divisionList.stream().map(this::mapToDivisionResponse).toList();
    }

    @Override
    public List<DivisionResponse> findAllStatusTrue() {
        List<Division> divisionList = divisionRepository.findAllByDivStatusIsTrue();
        return divisionList.stream().map(this::mapToDivisionResponse).toList();
    }

    @Override
    public DivisionResponse updateDivision(Long id, DivisionRequest updateDivisionRequest) throws ResourceNotFoundException {
        String divCode = updateDivisionRequest.getDivCode();
        Division existingDivision = this.findDivisionById(id);
        boolean exists = divisionRepository.existsByDivCode(divCode);
        if (!exists) {
            modelMapper.map(updateDivisionRequest, existingDivision);
            Division updatedDivision = divisionRepository.save(existingDivision);
            return mapToDivisionResponse(updatedDivision);
        }
        throw new ResourceNotFoundException("Division Already Exist");
    }

    @Override
    public void deleteDivisionId(Long id) throws ResourceNotFoundException {
        Division division = this.findDivisionById(id);
        divisionRepository.deleteById(division.getId());

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
