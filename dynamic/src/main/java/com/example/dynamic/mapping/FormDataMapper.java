package com.example.dynamic.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.dynamic.dto.request.FormDataRequest;
import com.example.dynamic.dto.response.FormDataResponse;
import com.example.dynamic.entity.FormData;

@Mapper(componentModel = "spring")
public interface FormDataMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "form", ignore = true)
	FormData mapToFormData(FormDataRequest formDataRequest);

	FormDataResponse mapToFormDataResponse(FormData formData);

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "form", ignore = true)
	void updateFormDataFromRequest(FormDataRequest formDataRequest, @MappingTarget FormData formData);
}
