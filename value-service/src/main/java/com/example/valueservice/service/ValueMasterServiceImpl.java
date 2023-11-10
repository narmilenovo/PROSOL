package com.example.valueservice.service;

import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.entity.ValueMaster;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.example.valueservice.repository.ValueMasterRepository;
import com.example.valueservice.service.interfaces.ValueMasterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValueMasterServiceImpl implements ValueMasterService {

    private final ValueMasterRepository valueMasterRepository;
    private final ModelMapper modelMapper;

    @Override
    public ValueMasterResponse saveValue(ValueMasterRequest valueMasterRequest) {
        ValueMaster valueMaster = modelMapper.map(valueMasterRequest, ValueMaster.class);
        ValueMaster savedValue = valueMasterRepository.save(valueMaster);
        return mapToValueMasterResponse(savedValue);
    }

    @Override
    public List<ValueMasterResponse> getAllValue() {
        List<ValueMaster> allValues = valueMasterRepository.findAll();
        return allValues.stream()
                .sorted(Comparator.comparing(ValueMaster::getId))
                .map(this::mapToValueMasterResponse)
                .toList();
    }

    @Override
    public ValueMasterResponse getValueById(Long id) throws ResourceNotFoundException {
        ValueMaster valueMaster = findValueById(id);
        return mapToValueMasterResponse(valueMaster);
    }

    @Override
    public ValueMasterResponse updateValue(Long id, ValueMasterRequest updateValueMasterRequest) throws ResourceNotFoundException {
        ValueMaster existingValueMaster = findValueById(id);
        modelMapper.map(updateValueMasterRequest, existingValueMaster);
        ValueMaster updatedValueMaster = valueMasterRepository.save(existingValueMaster);
        return mapToValueMasterResponse(updatedValueMaster);
    }

    @Override
    public void deleteValueId(Long id) throws ResourceNotFoundException {
        ValueMaster valueMaster = findValueById(id);
        valueMasterRepository.deleteById(valueMaster.getId());
    }

    private ValueMaster findValueById(Long id) throws ResourceNotFoundException {
        Optional<ValueMaster> valueMaster = valueMasterRepository.findById(id);
        if (valueMaster.isEmpty()) {
            throw new ResourceNotFoundException("Value Master with this ID Not found");
        }
        return valueMaster.get();
    }


    private ValueMasterResponse mapToValueMasterResponse(ValueMaster valueMaster) {
        return modelMapper.map(valueMaster, ValueMasterResponse.class);
    }
}
