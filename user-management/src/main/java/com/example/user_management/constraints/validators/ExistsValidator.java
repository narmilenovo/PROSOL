package com.example.user_management.constraints.validators;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.user_management.constraints.Exists;
import com.example.user_management.utils.Constants;
import com.example.user_management.utils.Helpers;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExistsValidator implements ConstraintValidator<Exists, Object> {

	private String propertyName;
	private String repositoryName;

	private final ApplicationContext applicationContext;

	@Override
	public void initialize(Exists constraintAnnotation) {
		propertyName = constraintAnnotation.property();
		repositoryName = constraintAnnotation.repository();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		Object result;
		String finalRepositoryName = Constants.REPOSITORY_PACKAGE + repositoryName;
		try {

			Class<?> type = Class.forName(finalRepositoryName);
			Object instance = this.applicationContext.getBean(finalRepositoryName);

			final Object propertyObj = BeanUtils.getProperty(value, propertyName);

			String finalPropertyName = Helpers.capitalize(propertyName);
			String methodName = "findBy" + finalPropertyName;

			result = type.getMethod(methodName, String.class).invoke(instance, propertyObj.toString());

		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();

			return false;
		}
		return result != null;

	}
}