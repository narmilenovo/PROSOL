package com.example.generalservice.service;

import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.entity.SalesUnit;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.SalesUnitRepository;
import com.example.generalservice.service.interfaces.SalesUnitService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesUnitServiceImpl implements SalesUnitService {
    private final SalesUnitRepository salesUnitRepository;
    private final ModelMapper modelMapper;

    @Override
    public SalesUnitResponse saveSalesUnit(SalesUnitRequest salesUnitRequest) throws ResourceFoundException {
        String salesCode = salesUnitRequest.getSalesCode();
        String salesName = salesUnitRequest.getSalesName();
        boolean exists = salesUnitRepository.existsBySalesCodeOrSalesName(salesCode, salesName);
        if (!exists) {

            SalesUnit salesUnit = modelMapper.map(salesUnitRequest, SalesUnit.class);
            SalesUnit savedSalesUnit = salesUnitRepository.save(salesUnit);
            return mapToSalesUnitResponse(savedSalesUnit);
        }
        throw new ResourceFoundException("Sales Unit already exists");
    }

    @Override
    public List<SalesUnitResponse> getAllSalesUnit() {
        List<SalesUnit> salesUnitList = salesUnitRepository.findAll();
        return salesUnitList.stream().map(this::mapToSalesUnitResponse).toList();
    }

    @Override
    public SalesUnitResponse getSalesUnitById(Long id) throws ResourceNotFoundException {
        SalesUnit salesUnit = this.findSalesUnitById(id);
        return mapToSalesUnitResponse(salesUnit);
    }

    @Override
    public List<SalesUnitResponse> findAllStatusTrue() {
        List<SalesUnit> salesUnits = salesUnitRepository.findAllBySalesStatusIsTrue();
        return salesUnits.stream().map(this::mapToSalesUnitResponse).toList();
    }

    @Override
    public SalesUnitResponse updateSalesUnit(Long id, SalesUnitRequest updateSalesUnitRequest) throws ResourceNotFoundException, ResourceFoundException {
        String salesCode = updateSalesUnitRequest.getSalesCode();
        String salesName = updateSalesUnitRequest.getSalesName();
        SalesUnit existingSalesUnit = this.findSalesUnitById(id);
        boolean exists = salesUnitRepository.existsBySalesCodeAndIdNotOrSalesNameAndIdNot(salesCode, id, salesName, id);
        if (!exists) {
            modelMapper.map(updateSalesUnitRequest, existingSalesUnit);
            SalesUnit updatedSalesUnit = salesUnitRepository.save(existingSalesUnit);
            return mapToSalesUnitResponse(updatedSalesUnit);
        }
        throw new ResourceFoundException("Sales Unit already exists");
    }

    @Override
    public void deleteSalesUnitId(Long id) throws ResourceNotFoundException {
        SalesUnit salesUnit = this.findSalesUnitById(id);
        salesUnitRepository.deleteById(salesUnit.getId());
    }

    private SalesUnitResponse mapToSalesUnitResponse(SalesUnit salesUnit) {
        return modelMapper.map(salesUnit, SalesUnitResponse.class);
    }

    private SalesUnit findSalesUnitById(Long id) throws ResourceNotFoundException {
        Optional<SalesUnit> salesUnit = salesUnitRepository.findById(id);
        if (salesUnit.isEmpty()) {
            throw new ResourceNotFoundException("No Sales Unit found with this Id");
        }
        return salesUnit.get();
    }
}
