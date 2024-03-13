package com.example.dynamic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.example.dynamic.dto.request.FormDataRequest;
import com.example.dynamic.dto.response.FormDataResponse;
import com.example.dynamic.dto.response.UploadFileResponse;
import com.example.dynamic.entity.Form;
import com.example.dynamic.entity.FormData;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.mapping.FormDataMapper;
import com.example.dynamic.repository.FormDataRepository;
import com.example.dynamic.repository.FormFieldRepository;
import com.example.dynamic.repository.FormRepository;
import com.example.dynamic.service.interfaces.FormDataService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormDataServiceImpl implements FormDataService {

	private final FormRepository formRepository;
	private final FormDataRepository formDataRepository;
	private final FormFieldRepository fieldRepository;
	private final FileStorageService fileStorageService;
	private final FormDataMapper formDataMapper;

	@Override
	public FormDataResponse submitFormData(String formName, FormDataRequest formDataRequest,
			MultiValueMap<String, MultipartFile> dynamicFiles) throws ResourceNotFoundException {

		Form form = getForm(formName);
		FormData formData = formDataMapper.mapToFormData(formDataRequest);
		formData.setForm(form);

		// Validate dynamic fields
		validateDynamicFields(formDataRequest.getDynamicFields(), formName);

		formData = formDataRepository.save(formData);

		if (dynamicFiles != null && !dynamicFiles.isEmpty()) {
			handleDynamicFiles(dynamicFiles, formName, formData);
		}

		return formDataMapper.mapToFormDataResponse(formData);
	}

	private Form getForm(String formName) throws ResourceNotFoundException {
		return formRepository.findByFormName(formName).orElseThrow(
				() -> new ResourceNotFoundException("Form with name: " + formName + " not Found in Db !!"));
	}

	private void validateDynamicFields(Map<String, Object> dynamicFields, String formName)
			throws ResourceNotFoundException {
		for (String fieldName : dynamicFields.keySet()) {
			if (!existFieldName(fieldName, formName)) {
				throw new ResourceNotFoundException(
						"Field with name '" + fieldName + "' does not exist in form '" + formName + "'.");
			}
		}
	}

	private void handleDynamicFiles(MultiValueMap<String, MultipartFile> dynamicFiles, String formName,
			FormData formData) {
		for (Map.Entry<String, List<MultipartFile>> entry : dynamicFiles.entrySet()) {
			String fileFieldName = entry.getKey();
			if (existFileFieldName(fileFieldName, formName)) {
				List<MultipartFile> files = entry.getValue();
				List<UploadFileResponse> fileResponses = new ArrayList<>();
				for (MultipartFile file : files) {
					String fileName = fileStorageService.storeFile(file, formName, formData.getId());
					UploadFileResponse fileResponse = new UploadFileResponse(fileName, file.getContentType(),
							file.getSize());
					fileResponses.add(fileResponse);
				}
				formData.getDynamicFields().put(fileFieldName,
						fileResponses.size() == 1 ? fileResponses.get(0) : fileResponses);
				formDataRepository.save(formData);
			}
		}
	}

	private boolean existFieldName(String fieldName, String formName) {
		return fieldRepository.existsByFieldNameAndForm_FormName(fieldName, formName);
	}

	private boolean existFileFieldName(String fieldName, String formName) {
		return fieldRepository.existsByFieldNameAndDataTypeAndForm_FormName(fieldName, "fileUpload", formName);
	}

}
