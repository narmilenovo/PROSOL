package com.example.plantservice.service;

import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.entity.PriceControl;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PriceControlRepo;
import com.example.plantservice.service.interfaces.PriceControlService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceControlServiceImpl implements PriceControlService {

    private final PriceControlRepo priceControlRepo;

    private final ModelMapper modelMapper;

    public static final String PRICE_CONTROL_NOT_FOUND_MESSAGE = null;


    @Override
    public List<PriceControlResponse> getAllPriceControl() {
        List<PriceControl> priceControl = priceControlRepo.findAll();
        return priceControl.stream().map(this::mapToPriceControlResponse).toList();
    }

    @Override
    public PriceControlResponse updatePriceControl(Long id, PriceControlRequest priceControlRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<PriceControl> existPriceControlName = priceControlRepo.findByPriceControlName(priceControlRequest.getPriceControlName());
        if (existPriceControlName.isPresent()&& !existPriceControlName.get().getPriceControlName().equals(priceControlRequest.getPriceControlName())) {
            throw new AlreadyExistsException("PriceControl with this name already exists");
        } else {
            PriceControl existingPriceControl = this.findPriceControlById(id);
            modelMapper.map(priceControlRequest, existingPriceControl);
            priceControlRepo.save(existingPriceControl);
            return mapToPriceControlResponse(existingPriceControl);
        }
    }

    public void deletePriceControl(Long id) throws ResourceNotFoundException {
        PriceControl pricecontrol = this.findPriceControlById(id);
        priceControlRepo.deleteById(pricecontrol.getId());
    }

    @Override
    public PriceControlResponse savePriceControl(PriceControlRequest priceControlRequest) throws  AlreadyExistsException {

        Optional<PriceControl> existPriceControlName = priceControlRepo.findByPriceControlName(priceControlRequest.getPriceControlName());
        if (existPriceControlName.isPresent()) {
            throw new AlreadyExistsException("PriceControl with this name already exists");
        } else {
            PriceControl priceControl = modelMapper.map(priceControlRequest, PriceControl.class);
            priceControlRepo.save(priceControl);
            return mapToPriceControlResponse(priceControl);
        }
    }


    private PriceControlResponse mapToPriceControlResponse(PriceControl pricecontrol) {
        return modelMapper.map(pricecontrol, PriceControlResponse.class);
    }

    private PriceControl findPriceControlById(Long id) throws ResourceNotFoundException {
        Optional<PriceControl> priceControl = priceControlRepo.findById(id);
        if (priceControl.isEmpty()) {
            throw new ResourceNotFoundException(PRICE_CONTROL_NOT_FOUND_MESSAGE);
        }
        return priceControl.get();
    }



    @Override
    public PriceControlResponse getPriceControlById(Long id) throws ResourceNotFoundException {
        PriceControl pricecontrol = this.findPriceControlById(id);
        return mapToPriceControlResponse(pricecontrol);
    }

    @Override
    public List<PriceControlResponse> updateBulkStatusPriceControlId(List<Long> id) {
        List<PriceControl> existingPriceControl = priceControlRepo.findAllById(id);
        for (PriceControl priceControl : existingPriceControl) {
            priceControl.setPriceControlStatus(!priceControl.getPriceControlStatus());
        }
        priceControlRepo.saveAll(existingPriceControl);
        return existingPriceControl.stream().map(this::mapToPriceControlResponse).toList();
    }

    @Override
    public PriceControlResponse updateStatusUsingPriceControlId(Long id) throws ResourceNotFoundException {
        PriceControl existingPriceControl = this.findPriceControlById(id);
        existingPriceControl.setPriceControlStatus(!existingPriceControl.getPriceControlStatus());
        priceControlRepo.save(existingPriceControl);
        return mapToPriceControlResponse(existingPriceControl);
    }
}
