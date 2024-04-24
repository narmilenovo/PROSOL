package com.example.dynamic.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.dto.response.FormResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;

public interface FormService {

	FormResponse createForm(FormRequest formRequest) throws ResourceFoundException;

	FormResponse getFormById(@NonNull Long id) throws ResourceNotFoundException;

	FormResponse getFormByName(String formName) throws ResourceNotFoundException;

	List<FormResponse> getAllForm();

	void deleteFormById(@NonNull Long id) throws ResourceNotFoundException;

}
