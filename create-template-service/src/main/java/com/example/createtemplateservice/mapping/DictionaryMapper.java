package com.example.createtemplateservice.mapping;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.createtemplateservice.client.DictionaryAllResponse;
import com.example.createtemplateservice.client.generalsettings.NmUomResponse;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.entity.Dictionary;

@Mapper(componentModel = "spring", uses = { DictionaryAttributeMapper.class })
public interface DictionaryMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "attributes", source = "attributes")
	Dictionary mapToDictionary(DictionaryRequest dictionaryRequest);

	DictionaryResponse mapToDictionaryResponse(Dictionary dictionary);

	@Mapping(target = "nmUoms", source = "nmUoms", qualifiedByName = "mapNmUoms")
	DictionaryAllResponse mapToDictionaryAll(Dictionary dictionary);

	@Named("mapNmUoms")
	default List<NmUomResponse> mapNmUoms(List<Long> nmUomIds) {
		if (nmUomIds == null || nmUomIds.isEmpty()) {
			return Collections.emptyList();
		}
		return nmUomIds.stream().map(id -> new NmUomResponse()).toList();
	}

}
