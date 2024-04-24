package com.example.dynamic.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.dynamic.DynamicApplicationTest;
import com.example.dynamic.entity.Form;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Import(DynamicApplicationTest.class)
class FormRepositoryTest {

	@Autowired
	private FormRepository formRepository;

	Form form;
	String formName;

	@BeforeEach
	void setUp() {
		// Given
		formName = "TestForm";
		form = new Form(null, formName, "Demo class for testing", new ArrayList<>());
	}

	@Test
	void testFindByFormNameToFound() {

		Form savedForm = formRepository.save(form);

		// When - Form is searched using FormName
		Form foundForm = formRepository.findByFormName(formName).get();

		// Then - Form is Present or Not
		assertThat(savedForm).isEqualTo(foundForm);
	}

	@Test
	void testFindByFormNameToNotFound() {

		// When - Form is searched using FormName
		Form foundForm = formRepository.findByFormName(formName).orElse(null);

		// Then - Form is Present or Not
		assertThat(foundForm).isNull();
	}

	@Test
	void testexistsByFormName() {
		formRepository.save(form);

		// When - Form is searched using FormName
		boolean exist = formRepository.existsByFormName(formName);

		// Then - Form is Present or Not
		assertThat(exist).isTrue();

	}

	@Test
	void testDoesNotexistsByFormName() {
		// When - Form is searched using FormName
		boolean exist = formRepository.existsByFormName(formName);

		// Then - Form is Present or Not
		assertThat(exist).isFalse();

	}

}
