package com.example.dynamic.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.dto.response.FormResponse;
import com.example.dynamic.entity.Form;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.mapping.FormMapper;
import com.example.dynamic.repository.FormRepository;
import com.example.dynamic.service.interfaces.FormService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormServiceImpl implements FormService {

	private final FormRepository formRepository;
	private final FormMapper formMapper;

	@Override
	public FormResponse createForm(FormRequest formRequest) throws ResourceFoundException {
		Form form = formMapper.mapToForm(formRequest);
		if (formRepository.existsByFormName(form.getFormName())) {
			throw new ResourceFoundException("Form with :" + form.getFormName() + " is already present !!");
		}
		Form savedForm = formRepository.save(form);
		return formMapper.mapToFormResponse(savedForm);
	}

	@Override
	public FormResponse getFormById(@NonNull Long id) throws ResourceNotFoundException {
		Form form = getById(id);
		return formMapper.mapToFormResponse(form);
	}

	@Override
	public FormResponse getFormByName(String formName) throws ResourceNotFoundException {
		Form form = getByName(formName);
		return formMapper.mapToFormResponse(form);
	}

	private Form getById(@NonNull Long id) throws ResourceNotFoundException {
		return formRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Form not found"));
	}

	private Form getByName(String formName) throws ResourceNotFoundException {
		return formRepository.findByFormName(formName)
				.orElseThrow(() -> new ResourceNotFoundException("Form not found"));
	}

	@Override
	public List<FormResponse> getAllForm() {
		return formRepository.findAll().stream().sorted(Comparator.comparing(Form::getId))
				.map(formMapper::mapToFormResponse).toList();
	}

	@Override
	public void deleteFormById(@NonNull Long id) throws ResourceNotFoundException {
		Form form = getById(id);
		if (form != null) {
			formRepository.delete(form);
		}
	}
}
