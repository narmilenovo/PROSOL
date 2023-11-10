package com.example.createtemplateservice.jpa.service.interfaces;

import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;

import java.util.List;

public interface DictionaryService {
    DictionaryResponse saveDictionary(DictionaryRequest dictionaryRequest);

    List<DictionaryResponse> getAllDictionary();

    DictionaryResponse getDictionaryById(Long id) throws ResourceNotFoundException;

    DictionaryResponse updateDictionary(Long id, DictionaryRequest updateDictionaryRequest) throws ResourceNotFoundException;

    void deleteDictionaryId(Long id) throws ResourceNotFoundException;
}
