package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.entity.AccAssignment;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.AccAssignmentRepository;
import com.example.sales_otherservice.service.interfaces.AccAssignmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccAssignmentServiceImpl implements AccAssignmentService {
    private final AccAssignmentRepository accAssignmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest) throws ResourceFoundException {
        String accCode = accAssignmentRequest.getAccCode();
        String accName = accAssignmentRequest.getAccName();
        boolean exists = accAssignmentRepository.existsByAccCodeOrAccName(accCode, accName);
        if (!exists) {

            AccAssignment accAssignment = modelMapper.map(accAssignmentRequest, AccAssignment.class);
            AccAssignment savedAssignment = accAssignmentRepository.save(accAssignment);
            return mapToAccAssignmentResponse(savedAssignment);
        }
        throw new ResourceFoundException("Acc assignment already exist");
    }

    @Override
    @Cacheable("acc")
    public List<AccAssignmentResponse> getAllAcc() {
        List<AccAssignment> accAssignments = accAssignmentRepository.findAll();
        return accAssignments.stream()
                .sorted(Comparator.comparing(AccAssignment::getId))
                .map(this::mapToAccAssignmentResponse)
                .toList();
    }

    @Override
    @Cacheable("acc")
    public AccAssignmentResponse getAccById(Long id) throws ResourceNotFoundException {
        AccAssignment accAssignment = this.findAccById(id);
        return mapToAccAssignmentResponse(accAssignment);
    }

    @Override
    @Cacheable("acc")
    public List<AccAssignmentResponse> findAllStatusTrue() {
        List<AccAssignment> list = accAssignmentRepository.findAllByAccStatusIsTrue();
        return list.stream()
                .sorted(Comparator.comparing(AccAssignment::getId))
                .map(this::mapToAccAssignmentResponse)
                .toList();
    }

    @Override
    public AccAssignmentResponse updateAcc(Long id, AccAssignmentRequest updateAccAssignmentRequest) throws ResourceNotFoundException, ResourceFoundException {
        AccAssignment existingAssignment = this.findAccById(id);
        String accCode = updateAccAssignmentRequest.getAccCode();
        String accName = updateAccAssignmentRequest.getAccName();
        boolean exists = accAssignmentRepository.existsByAccCodeAndIdNotOrAccNameAndIdNot(accCode, id, accName, id);
        if (!exists) {
            modelMapper.map(updateAccAssignmentRequest, existingAssignment);
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
