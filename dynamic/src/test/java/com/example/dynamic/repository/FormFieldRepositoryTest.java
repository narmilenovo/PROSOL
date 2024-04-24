package com.example.dynamic.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.dynamic.DynamicApplicationTest;
import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.Form;
import com.example.dynamic.entity.FormField;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Import(DynamicApplicationTest.class)
class FormFieldRepositoryTest {

	@Autowired
	private FormFieldRepository formFieldRepository;
	@Autowired
	private FormRepository formRepository;

	Form form1;
	String testform1;

	Form form2;
	String testform2;

	FormField formField1;
	String formCode;

	FormField formField2;
	String formDescription;

	FormField formField3;
	String country;

	FormField formField4;
	String gender;

	FormField formField5;
	String fileUpload;
	String fileInput;

	@BeforeEach
	void setUp() {
		// Given -
		testform1 = "TestForm1";
		form1 = new Form(null, testform1, "Form1 Desc", new ArrayList<>());

		testform2 = "TestForm2";
		form2 = new Form(null, testform2, "Form2 Desc", new ArrayList<>());

		formCode = "formCode";
		formField1 = new FormField(null, formCode, "textField", "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(), new ArrayList<>(),
				form1);

		formDescription = "formDescription";
		formField2 = new FormField(null, formDescription, "textArea", "234", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(), new ArrayList<>(),
				form1);

		country = "country";
		formField3 = new FormField(null, country, "dropDown", "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null,
				Arrays.asList(new DropDown(null, "India", formField3), new DropDown(null, "Australia", formField3)),
				new ArrayList<>(), form2);

		gender = "gender";
		formField4 = new FormField(null, gender, "radioButton", "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(),
				Arrays.asList("Male", "Female", "Other"), form2);

		fileInput = "fileInput";
		fileUpload = "fileUpload";
		formField4 = new FormField(null, fileInput, fileUpload, "text", new ArrayList<>(), 2L, 20L, Boolean.TRUE,
				Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, new ArrayList<>(), new ArrayList<>(),
				form2);
	}

	@Test
	void testFindAllByForm_FormName() {
		// Save Form1 and Form2
		formRepository.saveAll(List.of(form1, form2));

		// Save Form Fields 1,2,3 corresponding to Form
		formFieldRepository.saveAll(List.of(formField1, formField2, formField3));

		// When - Listing the different FormFields using FormName
		List<FormField> form1Fields = formFieldRepository.findAllByForm_FormName(testform1);
		List<FormField> form2Fields = formFieldRepository.findAllByForm_FormName(testform2);

		// Then - Checking the size of a form1Fields and form2Fields
		assertThat(form1Fields).hasSize(2);
		assertThat(form2Fields).hasSize(1);

	}

	@Test
	void testExistsByFieldNameAndForm_FormName() {
		// given
		formRepository.save(form1);
		formFieldRepository.save(formField1);
		// when
		boolean exists = formFieldRepository.existsByFieldNameAndForm_FormName(formCode, testform1);

		// then
		assertThat(exists).isTrue();
	}

	@Test
	void testExistsByFieldNameAndDataTypeAndForm_FormName() {
		// Given
		// save the form
		formRepository.save(form2);

		// Save the fileUpload formField
		formFieldRepository.save(formField4);

		// When - Check FileUpload field in FormName using dataType;
		boolean exists = formFieldRepository.existsByFieldNameAndDataTypeAndForm_FormName(fileInput, fileUpload,
				testform2);

		// Then - Result of the field is present or Not
		assertThat(exists).isTrue();
	}

	@Test
	void testExistsByFieldNameAndForm_FormNameAndIdNot() {
		// Given
		// Create a form
		formRepository.save(form1);

		// Create form fields
		FormField existingField = formField1;
		formFieldRepository.save(existingField);

		// When - Creating a new field with the same name but different id
		FormField newFieldWithSameName = new FormField();
		newFieldWithSameName.setFieldName(formCode); // Set the same field name
		newFieldWithSameName.setForm(form1); // Set the form
		formFieldRepository.save(newFieldWithSameName); // Ensure saving the new field

		boolean existsWithSameName = formFieldRepository.existsByFieldNameAndForm_FormNameAndIdNot(formCode, testform1,
				newFieldWithSameName.getId());

		// When - Creating a new field with a different name
		FormField newFieldWithDifferentName = formField2;
		formFieldRepository.save(newFieldWithDifferentName); // Ensure saving the new field

		boolean existsWithDifferentName = formFieldRepository.existsByFieldNameAndForm_FormNameAndIdNot(formDescription,
				testform1, newFieldWithDifferentName.getId());

		// Then
		assertThat(existsWithSameName).isTrue(); // Should exist with the same name but different id
		assertThat(existsWithDifferentName).isFalse(); // Should not exist with a different name
	}

}
