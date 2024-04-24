package com.example.dynamic.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.example.dynamic.DynamicApplicationTest;
import com.example.dynamic.client.AttributeServiceClient;
import com.example.dynamic.client.GeneralServiceClient;
import com.example.dynamic.client.MrpServiceClient;
import com.example.dynamic.client.PlantServiceClient;
import com.example.dynamic.client.SalesServiceClient;
import com.example.dynamic.client.SettingServiceClient;
import com.example.dynamic.client.ValueServiceClient;
import com.example.dynamic.client.VendorServiceClient;
import com.example.dynamic.dto.request.DropDownRequest;
import com.example.dynamic.dto.request.FormFieldRequest;
import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.Form;
import com.example.dynamic.entity.FormField;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.mapping.FormFieldMapper;
import com.example.dynamic.mapping.FormMapper;
import com.example.dynamic.repository.DropDownRepository;
import com.example.dynamic.repository.FormFieldRepository;
import com.example.dynamic.repository.FormRepository;

@ExtendWith(MockitoExtension.class)
@Import(DynamicApplicationTest.class)
class FormFieldServiceImplTest {

	@Mock
	private FormFieldRepository fieldRepository;
	@Mock
	private DropDownRepository dropDownRepository;
	@Mock
	private FormRepository formRepository;

	@Mock
	private FormMapper formMapper;
	@Mock
	private FormFieldMapper formFieldMapper;

	@Mock
	private MrpServiceClient mrpServiceClient;
	@Mock
	private PlantServiceClient plantServiceClient;
	@Mock
	private GeneralServiceClient generalServiceClient;
	@Mock
	private SalesServiceClient salesServiceClient;
	@Mock
	private VendorServiceClient vendorServiceClient;
	@Mock
	private SettingServiceClient settingServiceClient;
	@Mock
	private AttributeServiceClient attributeServiceClient;
	@Mock
	private ValueServiceClient valueServiceClient;

	@InjectMocks
	private FormFieldServiceImpl formFieldService;

	FormRequest form1;
	String testform1;

	FormRequest form2;
	String testform2;

	FormFieldRequest formField1;
	String textField;
	String formCode;

	FormFieldRequest formField2;
	String textArea;
	String formDescription;

	FormFieldRequest formField3;
	String dropDown;
	String country;

	FormFieldRequest formField4;
	String radioButton;
	String gender;

	FormFieldRequest formField5;
	String fileUpload;
	String fileInput;

	Form form;
	FormField formField;

	@BeforeEach
	void setUp() throws Exception {
		formFieldService = new FormFieldServiceImpl(fieldRepository, dropDownRepository, formRepository,
				formFieldMapper, mrpServiceClient, plantServiceClient, generalServiceClient, salesServiceClient,
				vendorServiceClient, settingServiceClient, attributeServiceClient, valueServiceClient);

		testform1 = "TestForm1";
		form1 = new FormRequest(testform1, "Form1 Desc");

		testform2 = "TestForm2";
		form2 = new FormRequest(testform2, "Form2 Desc");

		formCode = "formCode";
		textField = "textField";
		formField1 = new FormFieldRequest(formCode, textField, "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(), new ArrayList<>(),
				form1);

		formDescription = "formDescription";
		textArea = "textArea";
		formField2 = new FormFieldRequest(formDescription, textArea, "234", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(), new ArrayList<>(),
				form1);

		country = "country";
		dropDown = "dropDown";
		formField3 = new FormFieldRequest(country, dropDown, "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null,
				Arrays.asList(new DropDownRequest("India", formField3), new DropDownRequest("Australia", formField3)),
				new ArrayList<>(), form2);

		gender = "gender";
		radioButton = "radioButton";
		formField4 = new FormFieldRequest(gender, radioButton, "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(),
				Arrays.asList("Male", "Female", "Other"), form2);

		fileInput = "fileInput";
		fileUpload = "fileUpload";
		formField4 = new FormFieldRequest(fileInput, fileUpload, "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(), new ArrayList<>(),
				form2);

		form = new Form();
		formField = new FormField();
	}

	@Test
	void testCreateDynamicTextField() throws ResourceFoundException {
		// Given

		when(formRepository.findByFormName(anyString())).thenReturn(Optional.of(form));
		when(fieldRepository.existsByFieldNameAndForm_FormName(anyString(), anyString())).thenReturn(false);

		formField.setDataType(textField);
		when(formFieldMapper.mapToFormField(any(FormFieldRequest.class))).thenReturn(formField);

		formFieldService.createDynamicField(testform1, formField1);

		assertNotNull(formField);
		verify(fieldRepository).save(formField);

	}

	@Test
	void testCreateDynamicTextArea() throws ResourceFoundException {
		// Given
		when(formRepository.findByFormName(anyString())).thenReturn(Optional.of(form));
		when(fieldRepository.existsByFieldNameAndForm_FormName(anyString(), anyString())).thenReturn(false);

		formField.setDataType(textArea);
		when(formFieldMapper.mapToFormField(any(FormFieldRequest.class))).thenReturn(formField);

		formFieldService.createDynamicField(testform1, formField2);

		assertNotNull(formField);
		verify(fieldRepository).save(formField);

	}

	@Test
	void testCreateDynamicDropDown() throws ResourceFoundException {
		// Given

		when(formRepository.findByFormName(anyString())).thenReturn(Optional.of(form));
		when(fieldRepository.existsByFieldNameAndForm_FormName(anyString(), anyString())).thenReturn(false);
		formField.setDataType(dropDown);

		when(formFieldMapper.mapToFormField(any(FormFieldRequest.class))).thenReturn(formField);
		when(formFieldMapper.mapToDropDown(any(DropDownRequest.class))).thenAnswer(invocation -> {
			DropDownRequest request = invocation.getArgument(0);
			DropDown dropDown = new DropDown();
			dropDown.setOptionValue(request.getOptionValue());
			return dropDown;
		});

		formFieldService.createDynamicField(testform2, formField3);

		assertNotNull(formField);
		verify(fieldRepository).save(formField);

	}

	@Test
	void testCreateDynamicRadioButton() throws ResourceFoundException {
		// Given
		when(formRepository.findByFormName(anyString())).thenReturn(Optional.of(form));
		when(fieldRepository.existsByFieldNameAndForm_FormName(anyString(), anyString())).thenReturn(false);

		formField.setDataType(radioButton);
		when(formFieldMapper.mapToFormField(any(FormFieldRequest.class))).thenReturn(formField);

		formFieldService.createDynamicField(testform2, formField4);

		assertNotNull(formField);
		verify(fieldRepository).save(formField);

	}

	@Test
	void testCreateDynamicFileUpload() throws ResourceFoundException {
		// Given
		when(formRepository.findByFormName(anyString())).thenReturn(Optional.of(form));
		when(fieldRepository.existsByFieldNameAndForm_FormName(anyString(), anyString())).thenReturn(false);

		formField.setDataType(fileUpload);
		when(formFieldMapper.mapToFormField(any(FormFieldRequest.class))).thenReturn(formField);

		formFieldService.createDynamicField(testform2, formField4);

		assertNotNull(formField);
		verify(fieldRepository).save(formField);

	}

	@Test
	void testGetDynamicFieldById_WhenFieldExists() throws ResourceNotFoundException {
		// Arrange
		when(fieldRepository.findById(anyLong())).thenReturn(Optional.of(formField));

		// Act
		formFieldService.getDynamicFieldById(anyLong());

		// Assert
		verify(fieldRepository, times(1)).findById(anyLong());
	}

	@Test
	void testGetDynamicFieldById_WhenFieldDoesNotExist() {
		// Arrange
		when(fieldRepository.findById(anyLong())).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(ResourceNotFoundException.class, () -> formFieldService.getDynamicFieldById(anyLong()));
		verify(fieldRepository, times(1)).findById(anyLong());
	}

	@Test
	void testGetAllDynamicFieldsByForm() {

		formFieldService.getAllDynamicFieldsByForm(anyString());

		verify(fieldRepository, times(1)).findAllByForm_FormName(anyString());
	}

	@Test
	void testUpdateDynamicFieldById() throws ResourceNotFoundException, ResourceFoundException {
		Long id = 1L;
		FormFieldRequest updateFieldRequest = formField1;

		FormField existingFormField = new FormField();
		existingFormField.setId(id);
		when(fieldRepository.findById(any())).thenReturn(Optional.of(existingFormField));
		when(formRepository.findByFormName(any())).thenReturn(Optional.of(form));
		when(fieldRepository.save(any())).thenReturn(existingFormField);

		formFieldService.updateDynamicFieldById(testform1, id, updateFieldRequest);

		verify(fieldRepository, times(1)).save(any());
	}

	@Test
	void testRemoveDynamicFieldById() throws ResourceNotFoundException {
		when(fieldRepository.findById(anyLong())).thenReturn(Optional.of(formField));
		formFieldService.removeDynamicFieldById(anyLong());
		verify(fieldRepository).delete(any(FormField.class));
	}

	@Test
	void testRemoveDynamicFieldByIdNotFound() throws ResourceNotFoundException {
		when(fieldRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> formFieldService.removeDynamicFieldById(anyLong()));

	}

	@Test
	void testCheckFieldInForm() {
		formFieldService.checkFieldInForm(anyString(), anyString());

		verify(fieldRepository, times(1)).existsByFieldNameAndForm_FormName(anyString(), anyString());
	}
}
