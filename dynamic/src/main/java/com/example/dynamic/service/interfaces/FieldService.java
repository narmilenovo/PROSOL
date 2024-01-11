package com.example.dynamic.service.interfaces;

import java.util.List;

import com.example.dynamic.dto.request.FieldRequest;
import com.example.dynamic.dto.response.FieldResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;

public interface FieldService {

    FieldResponse createField(String formName, FieldRequest fieldRequest) throws ResourceFoundException;

    FieldResponse getFieldById(Long id) throws ResourceNotFoundException;

    List<FieldResponse> getAllFieldsByForm(String formName);

    FieldResponse updateFieldById(Long id, FieldRequest updateFieldRequest)
            throws ResourceNotFoundException, ResourceFoundException;

    void removeFieldById(Long id) throws ResourceNotFoundException;

    boolean checkFieldInForm(String fieldName, String fieldName2);

}
