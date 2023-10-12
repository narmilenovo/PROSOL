package com.example.generalservice.service;

import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.entity.BaseUOP;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.BaseUOPRepository;
import com.example.generalservice.service.interfaces.BaseUOPService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BaseUOPServiceImpl implements BaseUOPService {
    private final BaseUOPRepository baseUOPRepository;
    private final ModelMapper modelMapper;

    @Override
    public BaseUOPResponse saveUop(BaseUOPRequest baseUOPRequest) {
        BaseUOP baseUOP = modelMapper.map(baseUOPRequest, BaseUOP.class);
        BaseUOP savedUop = baseUOPRepository.save(baseUOP);
        return mapToBaseUOPResponse(savedUop);
    }

    @Override
    public List<BaseUOPResponse> getAllUop() {
        List<BaseUOP> uopList = baseUOPRepository.findAll();
        return uopList.stream().map(this::mapToBaseUOPResponse).toList();
    }

    @Override
    public BaseUOPResponse getUopById(Long id) throws ResourceNotFoundException {
        BaseUOP baseUOP = this.findUopById(id);
        return mapToBaseUOPResponse(baseUOP);
    }

    @Override
    public List<BaseUOPResponse> findAllStatusTrue() {
        List<BaseUOP> uopList = baseUOPRepository.findAllByUopStatusIsTrue();
        return uopList.stream().map(this::mapToBaseUOPResponse).toList();
    }

    @Override
    public BaseUOPResponse updateUop(Long id, BaseUOPRequest updateBaseUOPRequest) throws ResourceNotFoundException {
        String uopCode = updateBaseUOPRequest.getUopCode();
        BaseUOP existingBaseUOP = this.findUopById(id);
        boolean exists = baseUOPRepository.existsByUopCode(uopCode);
        if (!exists) {
            modelMapper.map(updateBaseUOPRequest, existingBaseUOP);
            BaseUOP updatedBaseUOP = baseUOPRepository.save(existingBaseUOP);
            return mapToBaseUOPResponse(updatedBaseUOP);
        }
        throw new ResourceNotFoundException("Uop Already Exist");
    }

    @Override
    public void deleteUopId(Long id) throws ResourceNotFoundException {
        BaseUOP baseUOP = this.findUopById(id);
        baseUOPRepository.deleteById(baseUOP.getId());
    }

    private BaseUOPResponse mapToBaseUOPResponse(BaseUOP baseUOP) {
        return modelMapper.map(baseUOP, BaseUOPResponse.class);
    }


    private BaseUOP findUopById(Long id) throws ResourceNotFoundException {
        Optional<BaseUOP> uop = baseUOPRepository.findById(id);
        if (uop.isEmpty()) {
            throw new ResourceNotFoundException("No Uop found with this Id");
        }
        return uop.get();
    }

}
