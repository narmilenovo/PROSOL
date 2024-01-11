package com.example.dynamic.service.interfaces;

import java.util.List;

import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.dto.response.FormResponse;
import com.example.dynamic.exceptions.ResourceNotFoundException;

public interface FormService {

    FormResponse createForm(FormRequest formRequest);

    FormResponse getFormById(Long id) throws ResourceNotFoundException;

    FormResponse getFormByName(String formName) throws ResourceNotFoundException;

    List<FormResponse> getAllForm();

    void deleteFormById(Long id) throws ResourceNotFoundException;

}
