package com.example.dynamic.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.dynamic.dto.request.FormFieldRequest;
import com.example.dynamic.dto.response.FormFieldResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;

public interface FormFieldService {

	FormFieldResponse createDynamicField(String formName, FormFieldRequest fieldRequest) throws ResourceFoundException;

	FormFieldResponse getDynamicFieldById(@NonNull Long id) throws ResourceNotFoundException;

	List<FormFieldResponse> getAllDynamicFieldsByForm(String formName);

	FormFieldResponse updateDynamicFieldById(String formName, @NonNull Long id, FormFieldRequest updateFieldRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void removeDynamicFieldById(@NonNull Long id) throws ResourceNotFoundException;

	boolean checkFieldInForm(String fieldName, String fieldName2);

	List<FormFieldResponse> getDynamicFieldsAndExistingFields(String formName, Boolean extraFields)
			throws ClassNotFoundException;

	List<String> getAllFieldNamesOfForm(String formName) throws ClassNotFoundException;

	List<Object> getListOfFieldNameValues(String displayName, String formName);

}
