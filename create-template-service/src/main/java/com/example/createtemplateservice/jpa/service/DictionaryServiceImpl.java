package com.example.createtemplateservice.jpa.service;

import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.entity.Dictionary;
import com.example.createtemplateservice.jpa.repository.DictionaryRepository;
import com.example.createtemplateservice.jpa.service.interfaces.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final ModelMapper modelMapper;

    @Override
    public DictionaryResponse saveDictionary(DictionaryRequest dictionaryRequest) {
        Dictionary dictionary = modelMapper.map(dictionaryRequest, Dictionary.class);
        Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        return mapToDictionaryResponse(savedDictionary);
    }

    @Override
    public List<DictionaryResponse> getAllDictionary() {
        List<Dictionary> dictionaries = dictionaryRepository.findAll();
        return dictionaries.stream()
                .sorted(Comparator.comparing(Dictionary::getId))
                .map(this::mapToDictionaryResponse)
                .toList();
    }

    @Override
    public DictionaryResponse getDictionaryById(Long id) throws ResourceNotFoundException {
        Dictionary dictionary = findDictionaryById(id);
        return mapToDictionaryResponse(dictionary);
    }

    @Override
    public DictionaryResponse updateDictionary(Long id, DictionaryRequest updateDictionaryRequest) throws ResourceNotFoundException {
        Dictionary existingDictionary = findDictionaryById(id);
        modelMapper.map(updateDictionaryRequest, existingDictionary);
        Dictionary updatedDictionary = dictionaryRepository.save(existingDictionary);
        return mapToDictionaryResponse(updatedDictionary);
    }

    @Override
    public void deleteDictionaryId(Long id) throws ResourceNotFoundException {
        Dictionary dictionary = findDictionaryById(id);
        dictionaryRepository.delete(dictionary);
    }

    private Dictionary findDictionaryById(Long id) throws ResourceNotFoundException {
        Optional<Dictionary> dictionary = dictionaryRepository.findById(id);
        if (dictionary.isEmpty()) {
            throw new ResourceNotFoundException("No Dictionary found with this Id");
        }
        return dictionary.get();
    }

    private DictionaryResponse mapToDictionaryResponse(Dictionary dictionary) {
        return modelMapper.map(dictionary, DictionaryResponse.class);
    }
}
