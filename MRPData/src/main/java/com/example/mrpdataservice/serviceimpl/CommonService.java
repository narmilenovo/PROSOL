package com.example.mrpdataservice.serviceimpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.mrpdataservice.response.FormFieldResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Service
public class CommonService {

	@PersistenceContext
	private EntityManager entityManager;

	public List<FormFieldResponse> getMrpExistingFields(String formName) throws ClassNotFoundException {
//		List<FormFieldResponse> formFields = new ArrayList<>();
//		List<Field> fields = this.getReflectionFields(formName);
//		IntStream.range(0, fields.size()).forEach(i -> {
//			Field field = fields.get(i);
//			if (!Modifier.isStatic(field.getModifiers())) {
//				final FormFieldResponse formField = new FormFieldResponse();
//				formField.setId(i + 1L); // Set the id from the index with offset 1
//				formField.setFieldName(field.getName());
//				formField.setDataType(field.getType().getSimpleName());
//				formField.setMin(this.getReflectFieldMin(field));
//				formField.setMax(this.getReflectFieldMax(field));
//				formField.setRequired(this.isReflectFieldMandatory(field));
//				formField.setExtraField(false);
//				formField.setReadable(true);
//				formField.setWritable(true);
//				formField.setShowAsColumn(true);
//				formFields.add(formField);
//			}
//		});
//
//		return formFields;

		List<FormFieldResponse> formFields = new ArrayList<>();
		List<Field> fields = this.getReflectionFields(formName);
		fields.forEach(field -> {
			if (!Modifier.isStatic(field.getModifiers())) {
				final FormFieldResponse formField = new FormFieldResponse();
				formField.setId(null); // Set the id from the index with offset 1
				formField.setFieldName(field.getName());
				formField.setDataType(field.getType().getSimpleName());
				formField.setMin(this.getReflectFieldMin(field));
				formField.setMax(this.getReflectFieldMax(field));
				formField.setIsRequired(this.isReflectFieldMandatory(field));
				formField.setIsExtraField(false);
				formField.setIsReadable(true);
				formField.setIsWritable(true);
				formFields.add(formField);
			}
		});
		return formFields;
	}

	public List<Field> getReflectionFields(String className) throws ClassNotFoundException {
		Class<?> clazz = Class.forName("com.example.mrpdataservice.entity." + className);

		List<Field> fields = new ArrayList<>();

		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.getName().contains("Status") || field.getName().contains("dynamicFields")) {
				continue;
			}
			fields.add(field);
		}
		return fields;
	}

	protected Long getReflectFieldMin(Field field) {
		if (field != null) {
			Min minAnnotation = field.getAnnotation(Min.class);
			if (minAnnotation != null) {
				return minAnnotation.value();
			}
		}
		return null;
	}

	protected Long getReflectFieldMax(Field field) {
		if (field != null) {
			Max maxAnnotation = field.getAnnotation(Max.class);
			if (maxAnnotation != null) {
				return maxAnnotation.value();
			}
		}
		return null;
	}

	protected Boolean isReflectFieldMandatory(Field field) {
		if (field != null) {
			for (Annotation annotation : field.getDeclaredAnnotations()) {
				if (annotation instanceof NotNull || annotation instanceof NotEmpty) {
					return true;
				}
			}
		}
		return false;
	}

	public Field getField(String formName, String fieldname) throws ClassNotFoundException {
		List<Field> fields = getReflectionFields(formName);
		if (fields != null) {
			for (Field field : fields) {
				if (field.getName().equals(fieldname)) {
					return field;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Object> getMrpListOfFieldNameValues(String fieldName, String formName) {
		String queryString = "SELECT f." + fieldName + " FROM " + formName + " f";
		return entityManager.createQuery(queryString).getResultList();
	}

}
