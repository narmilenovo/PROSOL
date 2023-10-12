package com.example.generalservice.service;

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.entity.AlternateUOM;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.AlternateUOMRepository;
import com.example.generalservice.service.interfaces.AlternateUOMService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlternateUOMServiceImpl implements AlternateUOMService {
    private final AlternateUOMRepository alternateUOMRepository;
    private final ModelMapper modelMapper;

    @Override
    public AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest) {
        AlternateUOM alternateUOM = modelMapper.map(alternateUOMRequest, AlternateUOM.class);
        AlternateUOM savedUom = alternateUOMRepository.save(alternateUOM);
        return mapToAlternateUOMResponse(savedUom);
    }

    @Override
    public List<AlternateUOMResponse> getAllUom() {
        List<AlternateUOM> uomList = alternateUOMRepository.findAll();
        return uomList.stream().map(this::mapToAlternateUOMResponse).toList();
    }

    @Override
    public AlternateUOMResponse getUomById(Long id) throws ResourceNotFoundException {
        AlternateUOM uom = this.findUomById(id);
        return mapToAlternateUOMResponse(uom);
    }

    @Override
    public List<AlternateUOMResponse> findAllStatusTrue() {
        List<AlternateUOM> uomList = alternateUOMRepository.findAllByUomStatusIsTrue();
        return uomList.stream().map(this::mapToAlternateUOMResponse).toList();
    }

    @Override
    public AlternateUOMResponse updateUom(Long id, AlternateUOMRequest updateAlternateUOMRequest) throws ResourceNotFoundException {
        String uomCode = updateAlternateUOMRequest.getUomCode();
        AlternateUOM existingUom = this.findUomById(id);
        boolean exists = alternateUOMRepository.existsByUomCode(uomCode);
        if (!exists) {
            modelMapper.map(updateAlternateUOMRequest, existingUom);
            AlternateUOM updatedUom = alternateUOMRepository.save(existingUom);
            return mapToAlternateUOMResponse(updatedUom);
        }
        throw new ResourceNotFoundException("Uom Already Exist");
    }

    @Override
    public void deleteUomId(Long id) throws ResourceNotFoundException {
        AlternateUOM uom = this.findUomById(id);
        alternateUOMRepository.deleteById(uom.getId());
    }

    private AlternateUOMResponse mapToAlternateUOMResponse(AlternateUOM alternateUOM) {
        return modelMapper.map(alternateUOM, AlternateUOMResponse.class);
    }

    private AlternateUOM findUomById(Long id) throws ResourceNotFoundException {
        Optional<AlternateUOM> uom = alternateUOMRepository.findById(id);
        if (uom.isEmpty()) {
            throw new ResourceNotFoundException("No Uom found with this");
        }
        return uom.get();
    }
}
