package com.example.dynamic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.example.dynamic.dto.request.FormDataRequest;
import com.example.dynamic.dto.response.FormDataResponse;
import com.example.dynamic.dto.response.UploadFileResponse;
import com.example.dynamic.entity.Form;
import com.example.dynamic.entity.FormData;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.repository.FieldRepository;
import com.example.dynamic.repository.FormDataRepository;
import com.example.dynamic.repository.FormRepository;
import com.example.dynamic.service.interfaces.FormDataService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormDataServiceImpl implements FormDataService {

	private final FormRepository formRepository;
	private final FormDataRepository formDataRepository;
	private final FieldRepository fieldRepository;
	private FileStorageService fileStorageService;
	private final ModelMapper modelMapper;

	@Override
	public FormDataResponse submitFormData(String formName, FormDataRequest formDataRequest,
			MultiValueMap<String, MultipartFile> dynamicFiles) throws ResourceNotFoundException {

		Form form = getForm(formName);
		FormData savedFormData = new FormData();

		FormData formData = formDataRepository.save(new FormData());
		modelMapper.map(formDataRequest, formData);
		formData.setForm(form);

		for (Map.Entry<String, Object> entryField : formDataRequest.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();

			if (!existFieldName(fieldName, formName)) {
				throw new ResourceNotFoundException(
						"Field with name '" + fieldName + "' does not exists in form '" + formName + "'.");
			}
		}
		savedFormData = formDataRepository.save(formData);

		if (dynamicFiles != null && !dynamicFiles.isEmpty()) {
			for (Map.Entry<String, List<MultipartFile>> entry : dynamicFiles.entrySet()) {
				String fileFieldName = entry.getKey();
				if (!existFileFieldName(fileFieldName, formName)) {
					throw new ResourceNotFoundException(
							"Field with name '" + fileFieldName + "' does not exists in form '" + formName + "'.");
				} else {
					List<MultipartFile> files = entry.getValue();
					if (files.size() == 1) {
						// If there's only one file, store it directly as a value
						MultipartFile file = files.get(0);
						String fileName = fileStorageService.storeFile(file, formName, formData.getId());
						UploadFileResponse fileResponse = new UploadFileResponse(fileName, file.getContentType(),
								file.getSize());
						formData.getDynamicFields().put(fileFieldName, fileResponse);
					} else {
						List<UploadFileResponse> fileResponses = new ArrayList<>();
						for (MultipartFile file : files) {
							String fileName = fileStorageService.storeFile(file, formName, formData.getId());
							UploadFileResponse fileResponse = new UploadFileResponse(fileName, file.getContentType(),
									file.getSize());
							fileResponses.add(fileResponse);
						}
						formData.getDynamicFields().put(fileFieldName, fileResponses);
					}

				}
			}
			savedFormData = formDataRepository.save(formData);
		}

		return this.mapToFormDataResponse(savedFormData);
	}

	private Form getForm(String formName) throws ResourceNotFoundException {
		Optional<Form> existingForm = formRepository.findByFormName(formName);
		return existingForm.orElseThrow(
				() -> new ResourceNotFoundException("Form with name: " + formName + " not Found in Db !!"));
	}

	private FormDataResponse mapToFormDataResponse(FormData formData) {
		return modelMapper.map(formData, FormDataResponse.class);
	}

	private boolean existFieldName(String fieldName, String formName) {
		return fieldRepository.existsByFieldNameAndForm_FormName(fieldName, formName);
	}

	private boolean existFileFieldName(String fieldName, String formName) {
		return fieldRepository.existsByFieldNameAndDataTypeAndForm_FormName(fieldName, "fileUpload", formName);
	}

}
