package com.example.dynamic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.example.dynamic.DynamicApplicationTest;
import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.entity.Form;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.mapping.FormMapper;
import com.example.dynamic.repository.FormRepository;

@ExtendWith(MockitoExtension.class)
@Import(DynamicApplicationTest.class)
class FormServiceImplTest {

	@Mock
	private FormRepository formRepository;

	@Mock
	private FormMapper formMapper;

	@InjectMocks
	private FormServiceImpl formService;

	FormRequest formRequest;
	String formName;
	Form form;

	@BeforeEach
	void setUp() throws Exception {
		formService = new FormServiceImpl(formRepository, formMapper);
		formName = "TestForm";
		formRequest = new FormRequest(formName, "Demo class for testing");
		form = new Form();
	}

	@Test
	void testCreateForm() throws ResourceFoundException {
		// Given - Parsing data from request to Form
		when(formMapper.mapToForm(any(FormRequest.class))).thenReturn(form);
		when(formRepository.existsByFormName(form.getFormName())).thenReturn(false);

		// When - Create a form
		formService.createForm(formRequest);

		// Then - Form is created
		ArgumentCaptor<Form> formArgumentCaptor = ArgumentCaptor.forClass(Form.class);

		verify(formRepository).save(formArgumentCaptor.capture());

		Form capturedForm = formArgumentCaptor.getValue();
		assertThat(capturedForm).isEqualTo(form);
	}

	@Test
	void testCreateDuplicateForm() throws ResourceFoundException {
		// Given - Parsing data from request to Form
		when(formMapper.mapToForm(any(FormRequest.class))).thenReturn(form);
		when(formRepository.existsByFormName(form.getFormName())).thenReturn(true);

		assertThatThrownBy(() -> formService.createForm(formRequest)).isInstanceOf(ResourceFoundException.class)
				.hasMessageContaining("Form with :" + form.getFormName() + " is already present !!");
	}

	@Test
	void testGetFormById() throws ResourceNotFoundException {
		// Given - Form Id
		// When
		when(formRepository.findById(anyLong())).thenReturn(Optional.of(form));
		formService.getFormById(anyLong());
		// Then
		verify(formRepository).findById(anyLong());
	}

	@Test
	void testGetFormByIdNotFound() throws ResourceNotFoundException {
		// Given - Form Id
		// empty optional
		when(formRepository.findById(anyLong())).thenReturn(Optional.empty());
		// When
		assertThatThrownBy(() -> formService.getFormById(anyLong())).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Form not found");

		verify(formRepository).findById(anyLong());
	}

	@Test
	void testGetFormByName() throws ResourceNotFoundException {
		// When
		when(formRepository.findByFormName(anyString())).thenReturn(Optional.of(form));

		formService.getFormByName(anyString());

		// Then
		verify(formRepository).findByFormName(anyString());

	}

	@Test
	void testGetFormByNameNotFound() throws ResourceNotFoundException {

		when(formRepository.findByFormName(anyString())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> formService.getFormByName(anyString())).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Form not found");

		verify(formRepository).findByFormName(anyString());

	}

	@Test
	void testGetAllForm() {
		// When
		formService.getAllForm();
		// Then
		verify(formRepository).findAll();
	}

	@Test
	void testDeleteFormById() throws ResourceNotFoundException {
		// Given

		when(formRepository.findById(anyLong())).thenReturn(Optional.of(form));

		// When
		formService.deleteFormById(anyLong());

		// Then
		verify(formRepository).delete(any(Form.class));
	}

	@Test
	void testDeleteFormByIdNotFound() throws ResourceNotFoundException {
		// Given
		when(formRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> formService.getFormById(anyLong())).isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("Form not found");
	}

}
