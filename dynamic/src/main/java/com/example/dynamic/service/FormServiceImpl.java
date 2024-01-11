package com.example.dynamic.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.dto.response.FormResponse;
import com.example.dynamic.entity.Form;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.repository.FormRepository;
import com.example.dynamic.service.interfaces.FormService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {

	private final FormRepository formRepository;
	private final ModelMapper modelMapper;

	@Override
	public FormResponse createForm(FormRequest formRequest) {
		Form form = modelMapper.map(formRequest, Form.class);
		Form savedForm = formRepository.save(form);
		return this.mapToFormResponse(savedForm);
	}

	@Override
	public FormResponse getFormById(Long id) throws ResourceNotFoundException {
		Form form = getById(id);
		return this.mapToFormResponse(form);
	}

	@Override
	public FormResponse getFormByName(String formName) throws ResourceNotFoundException {
		Form form = getByName(formName);
		return this.mapToFormResponse(form);
	}

	private FormResponse mapToFormResponse(Form form) {
		return modelMapper.map(form, FormResponse.class);
	}

	private Form getById(Long id) throws ResourceNotFoundException {
		// Implement the code to retrieve the form by id from the form repository
		Optional<Form> optionalForm = formRepository.findById(id);

		// Throw an exception if the form is not found
		if (!optionalForm.isPresent()) {
			throw new ResourceNotFoundException("Form not found");
		}

		// Return the form
		return optionalForm.get();
	}

	private Form getByName(String formName) throws ResourceNotFoundException {
		// Implement the code to retrieve the form by id from the form repository
		Optional<Form> optionalForm = formRepository.findByFormName(formName);

		// Throw an exception if the form is not found
		if (!optionalForm.isPresent()) {
			throw new ResourceNotFoundException("Form not found");
		}

		// Return the form
		return optionalForm.get();
	}

	@Override
	public List<FormResponse> getAllForm() {
		List<Form> forms = formRepository.findAll();
		return forms
				.stream()
				.sorted(Comparator.comparing(Form::getId))
				.map(this::mapToFormResponse)
				.toList();
	}

	@Override
	public void deleteFormById(Long id) throws ResourceNotFoundException {
		Form form = getById(id);
		formRepository.delete(form);
	}

}
