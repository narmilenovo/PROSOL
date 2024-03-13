package com.example.dynamic.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.dynamic.dto.request.DropDownRequest;
import com.example.dynamic.dto.request.FormFieldRequest;
import com.example.dynamic.dto.response.FormFieldResponse;
import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.FormField;

@Mapper(componentModel = "spring")
public interface FormFieldMapper {

//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updatedAt", ignore = true)
//	@Mapping(target = "updatedBy", ignore = true)
//	@Mapping(target = "dropDowns", ignore = true)
//	@Mapping(target = "form", ignore = true)
//	@Mapping(target = "id", ignore = true)
	FormField mapToFormField(FormFieldRequest formFieldRequest);

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "dropDowns", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updatedAt", ignore = true)
//	@Mapping(target = "updatedBy", ignore = true)
//	@Mapping(target = "form", ignore = true)
	void updateFormFieldFromRequest(FormFieldRequest request, @MappingTarget FormField field);

//	@Mapping(target = "id", ignore = true)
	DropDown mapToDropDown(DropDownRequest dropDownRequest);

	FormFieldResponse mapToFieldResponse(FormField formField);
}
