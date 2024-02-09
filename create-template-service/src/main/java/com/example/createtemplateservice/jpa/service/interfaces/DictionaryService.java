package com.example.createtemplateservice.jpa.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.createtemplateservice.client.DictionaryAllResponse;
import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;

public interface DictionaryService {
	DictionaryResponse saveDictionary(DictionaryRequest dictionaryRequest, MultipartFile file);

	DictionaryResponse getDictionaryById(Long id, String show) throws ResourceNotFoundException;

	DictionaryAllResponse getDictionaryNmUomById(Long id, String show) throws ResourceNotFoundException;

	List<DictionaryResponse> getAllDictionary(String show) throws ResourceNotFoundException;

	List<DictionaryAllResponse> getAllDictionaryNmUom(String show) throws ResourceNotFoundException;

	List<String> getNounSuggestions(String noun);

	List<String> getModifiersByNoun(String noun);

	DictionaryResponse getRecordByNounAndModifer(String noun, String modifier);

	DictionaryResponse updateDictionary(Long id, DictionaryRequest updateDictionaryRequest, MultipartFile file)
			throws ResourceNotFoundException;

	void deleteDictionaryId(Long id) throws ResourceNotFoundException;

	void deleteBatchDictionary(List<Long> ids) throws ResourceNotFoundException;

}
