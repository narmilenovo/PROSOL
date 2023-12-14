package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.entity.SalesOrganization;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.SalesOrganizationRepository;
import com.example.sales_otherservice.service.interfaces.SalesOrganizationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesOrganizationServiceImpl implements SalesOrganizationService {
    private final SalesOrganizationRepository salesOrganizationRepository;
    private final ModelMapper modelMapper;

    @Override
    public SalesOrganizationResponse saveSo(SalesOrganizationRequest salesOrganizationRequest) throws ResourceFoundException {
        String soCode = salesOrganizationRequest.getSoCode();
        String soName = salesOrganizationRequest.getSoName();
        boolean exists = salesOrganizationRepository.existsBySoCodeOrSoName(soCode, soName);
        if (!exists) {

            SalesOrganization salesOrganization = modelMapper.map(salesOrganizationRequest, SalesOrganization.class);
            SalesOrganization savedSalesOrganization = salesOrganizationRepository.save(salesOrganization);
            return mapToSalesOrganizationResponse(savedSalesOrganization);
        }
        throw new ResourceFoundException("Sales Organization Already exists");
    }

    @Override
    public List<SalesOrganizationResponse> getAllSo() {
        List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAll();
        return salesOrganizations.stream()
                .sorted(Comparator.comparing(SalesOrganization::getId))
                .map(this::mapToSalesOrganizationResponse)
                .toList();
    }

    @Override
    public SalesOrganizationResponse getSoById(Long id) throws ResourceNotFoundException {
        SalesOrganization salesOrganization = this.findSoById(id);
        return mapToSalesOrganizationResponse(salesOrganization);
    }

    @Override
    public List<SalesOrganizationResponse> findAllStatusTrue() {
        List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAllBySoStatusIsTrue();
        return salesOrganizations.stream()
                .sorted(Comparator.comparing(SalesOrganization::getId))
                .map(this::mapToSalesOrganizationResponse)
                .toList();
    }

    @Override
    public SalesOrganizationResponse updateSo(Long id, SalesOrganizationRequest updateSalesOrganizationRequest) throws ResourceNotFoundException, ResourceFoundException {
        String soCode = updateSalesOrganizationRequest.getSoCode();
        String soName = updateSalesOrganizationRequest.getSoName();
        SalesOrganization existingSalesOrganization = this.findSoById(id);
        boolean exists = salesOrganizationRepository.existsBySoCodeAndIdNotOrSoNameAndIdNot(soCode, id, soName, id);
        if (!exists) {
            modelMapper.map(updateSalesOrganizationRequest, existingSalesOrganization);
            SalesOrganization updatedSalesOrganization = salesOrganizationRepository.save(existingSalesOrganization);
            return mapToSalesOrganizationResponse(updatedSalesOrganization);
        }
        throw new ResourceFoundException("Sales Organization Already exists");
    }

    @Override
    public void deleteSoById(Long id) throws ResourceNotFoundException {
        SalesOrganization salesOrganization = this.findSoById(id);
        salesOrganizationRepository.deleteById(salesOrganization.getId());
    }

    private SalesOrganizationResponse mapToSalesOrganizationResponse(SalesOrganization salesOrganization) {
        return modelMapper.map(salesOrganization, SalesOrganizationResponse.class);
    }

    private SalesOrganization findSoById(Long id) throws ResourceNotFoundException {
        Optional<SalesOrganization> salesOrganization = salesOrganizationRepository.findById(id);
        if (salesOrganization.isEmpty()) {
            throw new ResourceNotFoundException("Sales Organization Key not found with this Id");
        }
        return salesOrganization.get();
    }
}
