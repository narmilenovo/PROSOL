package com.example.dynamic.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.dynamic.dto.request.DropDownRequest;
import com.example.dynamic.dto.request.FormFieldRequest;
import com.example.dynamic.dto.response.FormFieldResponse;
import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.FormField;

@Mapper(componentModel = "spring", uses = { FormMapper.class })
public interface FormFieldMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "dropDowns", source = "dropDowns")
	@Mapping(target = "form", source = "form")
	FormField mapToFormField(FormFieldRequest formFieldRequest);

	@Mapping(target = "id", ignore = true)
	DropDown mapToDropDown(DropDownRequest dropDownRequest);
	
	List<DropDown> mapDropDownValues(List<DropDownRequest> dropDownRequests);

	FormFieldResponse mapToFieldResponse(FormField formField);
}
