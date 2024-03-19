package com.example.dynamic.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.dto.response.FormResponse;
import com.example.dynamic.entity.Form;

@Mapper(componentModel = "spring")
public interface FormMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "formFields", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	Form mapToForm(FormRequest formRequest);

	FormResponse mapToFormResponse(Form form);
}
