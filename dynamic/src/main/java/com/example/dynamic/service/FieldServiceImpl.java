package com.example.dynamic.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dynamic.dto.request.DropDownRequest;
import com.example.dynamic.dto.request.FieldRequest;
import com.example.dynamic.dto.response.FieldResponse;
import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.Field;
import com.example.dynamic.entity.Form;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.repository.FieldRepository;
import com.example.dynamic.repository.FormRepository;
import com.example.dynamic.service.interfaces.FieldService;
import com.example.dynamic.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

	private final FieldRepository fieldRepository;
	private final FormRepository formRepository;
	private final ModelMapper modelMapper;

	public static final String TEXT_FIELD_TYPE = "textField";
	public static final String TEXT_AREA_TYPE = "textArea";
	public static final String DATE_TYPE = "date";
	public static final String OBJECT_TYPE = "object";
	public static final String CHECKBOX_TYPE = "radioButton";
	public static final String DROPDOWN_TYPE = "dropDown";
	public static final String FILE_TYPE = "fileUpload";
	private static final Map<Class<?>[], String> REACT_TYPES;

	static {
		REACT_TYPES = new HashMap<>();
		REACT_TYPES.put(new Class[] { String.class, Integer.class, Float.class, Double.class }, TEXT_FIELD_TYPE);
		REACT_TYPES.put(new Class[] { String.class }, TEXT_AREA_TYPE);
		REACT_TYPES.put(new Class[] { Date.class, DateTime.class }, DATE_TYPE);
		REACT_TYPES.put(new Class[] { Boolean.class }, CHECKBOX_TYPE);
		REACT_TYPES.put(new Class[] { Enum.class, List.class }, DROPDOWN_TYPE);
		REACT_TYPES.put(new Class[] { Object.class }, OBJECT_TYPE);
	}

	private String setFieldConversion(String dataType) {
		for (Map.Entry<Class<?>[], String> entry : REACT_TYPES.entrySet()) {
			String value = entry.getValue();
			if (value.equalsIgnoreCase(dataType)) {
				return value;
			}
			if (dataType.equalsIgnoreCase(FILE_TYPE)) {
				return FILE_TYPE;
			}
		}
		return OBJECT_TYPE; // or throw an exception if no matching data type is found
	}

	@Override
	@Transactional
	public FieldResponse createField(String formName, FieldRequest fieldRequest) throws ResourceFoundException {
		Field field = modelMapper.map(fieldRequest, Field.class);
		// Mapping Forms
		Form form = getOrCreateForm(formName);
		field.setForm(form);

		conversionEquals(field);
		// Map and set drop-down values
		List<DropDown> dropDownValues = mapDropDownValues(fieldRequest.getDropDowns(), field);
		field.setDropDowns(dropDownValues);
		// Check Field Already exists
		boolean exists = this.checkFieldInForm(field.getFieldName(), form.getFormName());
		if (exists) {
			throw new ResourceFoundException("Field with name '" + field.getFieldName() + "' already exists in form '"
					+ form.getFormName() + "'.");
		}
		// Save the main form field entity
		Field savedField = fieldRepository.save(field);
		return mapToFieldResponse(savedField);
	}

	@Override
	public FieldResponse getFieldById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Field field = this.getById(id);
		return mapToFieldResponse(field);
	}

	@Override
	public List<FieldResponse> getAllFieldsByForm(String formName) {
		List<Field> fields = fieldRepository.findAllByForm_FormName(formName);
		return fields.stream().sorted(Comparator.comparing(Field::getId)).map(this::mapToFieldResponse).toList();
	}

	@Override
	@Transactional
	public FieldResponse updateFieldById(Long id, FieldRequest updateFieldRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Field existingField = getById(id);
		modelMapper.map(updateFieldRequest, existingField);
		existingField.setId(id);
		conversionEquals(existingField);
		// Map and set drop-down values
		List<DropDown> dropDownValues = mapDropDownValues(updateFieldRequest.getDropDowns(), existingField);
		existingField.setDropDowns(dropDownValues);

		// Update field by checking
		String formName = existingField.getForm().getFormName();
		boolean exists = this.checkNotIdFieldInForm(existingField.getFieldName(), formName, id);
		if (exists) {
			throw new ResourceFoundException("Field with name '" + existingField.getFieldName()
					+ "' already exists in form '" + formName + "'.");
		}
		Field updatedField = fieldRepository.save(existingField);
		return mapToFieldResponse(updatedField);
	}

	private List<DropDown> mapDropDownValues(List<DropDownRequest> dropDownRequests, Field field) {
		if (dropDownRequests != null) {
			return dropDownRequests.stream().map(dropDownRequest -> {
				DropDown dropDownValue = modelMapper.map(dropDownRequest, DropDown.class);
				dropDownValue.setField(field);
				// dropDownRepository.save(dropDownValue); // Optional
				return dropDownValue;
			}).toList();
		}
		return Collections.emptyList();
	}

	private Form getOrCreateForm(String formName) {
		Optional<Form> existingForm = formRepository.findByFormName(formName);

		return existingForm.orElseGet(() -> {
			Form newForm = new Form();
			newForm.setFormName(formName);
			return formRepository.save(newForm);
		});
	}

	@Override
	public void removeFieldById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Field field = this.getById(id);
		if (field != null) {
			fieldRepository.delete(field);
		}
	}

	private void conversionEquals(Field field) {
		field.setDataType(setFieldConversion(field.getDataType()));
		if (TEXT_FIELD_TYPE.equalsIgnoreCase(field.getDataType())
				|| TEXT_AREA_TYPE.equalsIgnoreCase(field.getDataType())
				|| FILE_TYPE.equalsIgnoreCase(field.getDataType())) {
			field.setDropDowns(null);
			field.setEnums(null);
		} else if (DROPDOWN_TYPE.equalsIgnoreCase(field.getDataType())) {
			field.setEnums(null);
		} else if (CHECKBOX_TYPE.equalsIgnoreCase(field.getDataType())) {
			field.setDropDowns(null);
		} else if (field.getEnums() == null) {
			field.setEnums(new ArrayList<>());
		} else if (field.getDropDowns() == null) {
			field.setDropDowns(new ArrayList<>());
		}

	}

	private Field getById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<Field> field = fieldRepository.findById(id);
		if (field.isEmpty()) {
			throw new ResourceNotFoundException("Field is not present");
		}

		return field.get();

	}

	private FieldResponse mapToFieldResponse(Field field) {
		return modelMapper.map(field, FieldResponse.class);
	}

	@Override
	public boolean checkFieldInForm(String fieldName, String formName) {
		return fieldRepository.existsByFieldNameAndForm_FormName(fieldName, formName);
	}

	private boolean checkNotIdFieldInForm(String fieldName, String formName, Long id) {
		return fieldRepository.existsByFieldNameAndForm_FormNameAndIdNot(fieldName, formName, id);
	}

}
