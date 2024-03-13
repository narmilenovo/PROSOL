package com.example.createtemplateservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.createtemplateservice.client.DictionaryAllResponse;
import com.example.createtemplateservice.client.DictionaryAttributeAllResponse;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryAttributeResponse;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.entity.Dictionary;
import com.example.createtemplateservice.jpa.entity.DictionaryAttribute;

@Mapper(componentModel = "spring")
public interface DictionaryMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
//	@Mapping(target = "attributes", ignore = true)
	Dictionary mapToDictionary(DictionaryRequest dictionaryRequest);

	DictionaryResponse mapToDictionaryResponse(Dictionary dictionary);

	DictionaryAttributeResponse mapToDictionaryAttributeResponse(DictionaryAttribute dictionaryAttribute);

	@Mapping(target = "nmUoms", ignore = true)
	DictionaryAllResponse mapToDictionaryAll(Dictionary dictionary);

//	@Mapping(target = "attrUoms", ignore = true)
//	@Mapping(target = "attribute", ignore = true)
//	@Mapping(target = "values", ignore = true)
	DictionaryAttributeAllResponse mapToDictionaryAttributeAllResponse(DictionaryAttribute dictionaryAttribute);
}
