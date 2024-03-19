package com.example.createtemplateservice.mapping;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.createtemplateservice.client.AttributeUomResponse;
import com.example.createtemplateservice.client.DictionaryAttributeAllResponse;
import com.example.createtemplateservice.client.valuemaster.ValueAttributeUom;
import com.example.createtemplateservice.jpa.dto.request.DictionaryAttributeRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryAttributeResponse;
import com.example.createtemplateservice.jpa.entity.DictionaryAttribute;

@Mapper(componentModel = "spring")
public interface DictionaryAttributeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "dictionary", ignore = true)
	DictionaryAttribute mapToDictionaryAttribute(DictionaryAttributeRequest dictionaryAttributeRequest);

	DictionaryAttributeResponse mapToDictionaryAttributeResponse(DictionaryAttribute dictionaryAttribute);

	@Mapping(target = "attribute.id", source = "attributeId")
	@Mapping(target = "values", source = "valueId", qualifiedByName = "mapValues")
	@Mapping(target = "attrUoms", source = "attrUomId", qualifiedByName = "mapAttrUoms")
	DictionaryAttributeAllResponse mapToDictionaryAttributeAllResponse(DictionaryAttribute dictionaryAttribute);

	@Named("mapValues")
	default List<ValueAttributeUom> mapValues(List<Long> valueIds) {
		if (valueIds == null || valueIds.isEmpty()) {
			return Collections.emptyList();
		}
		return valueIds.stream().map(id -> new ValueAttributeUom()).toList();
	}

	@Named("mapAttrUoms")
	default List<AttributeUomResponse> mapAttrUoms(List<Long> attrUomIds) {
		if (attrUomIds == null || attrUomIds.isEmpty())
			return Collections.emptyList();

		return attrUomIds.stream().map(id -> new AttributeUomResponse()).toList();
	}
}
