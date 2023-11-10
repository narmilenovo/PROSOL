package com.example.valueservice.service.interfaces;

import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface ValueMasterService {
    ValueMasterResponse saveValue(ValueMasterRequest valueMasterRequest);

    List<ValueMasterResponse> getAllValue();

    ValueMasterResponse getValueById(Long id) throws ResourceNotFoundException;

    ValueMasterResponse updateValue(Long id, ValueMasterRequest updateValueMasterRequest) throws ResourceNotFoundException;

    void deleteValueId(Long id) throws ResourceNotFoundException;
}
