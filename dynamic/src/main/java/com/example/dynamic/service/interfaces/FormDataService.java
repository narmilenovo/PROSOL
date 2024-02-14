package com.example.dynamic.service.interfaces;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.example.dynamic.dto.request.FormDataRequest;
import com.example.dynamic.dto.response.FormDataResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;

public interface FormDataService {

	FormDataResponse submitFormData(String formName, FormDataRequest formDataRequest,
			MultiValueMap<String, MultipartFile> dynamicFiles) throws ResourceNotFoundException, ResourceFoundException;

}
