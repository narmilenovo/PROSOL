package com.example.createtemplateservice.jpa.service;

import com.example.createtemplateservice.client.AttributeMaster.AttributeClient;
import com.example.createtemplateservice.client.AttributeMaster.AttributeMasterUomResponse;
import com.example.createtemplateservice.client.DictionaryAllResponse;
import com.example.createtemplateservice.client.DictionaryAttributeAllResponse;
import com.example.createtemplateservice.client.GeneralSettings.AttributeUom;
import com.example.createtemplateservice.client.GeneralSettings.GeneralSettingClient;
import com.example.createtemplateservice.client.GeneralSettings.NmUom;
import com.example.createtemplateservice.client.ValueMaster.ValueAttributeUom;
import com.example.createtemplateservice.client.ValueMaster.ValueMasterClient;
import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryAttributeResponse;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.entity.Dictionary;
import com.example.createtemplateservice.jpa.entity.DictionaryAttribute;
import com.example.createtemplateservice.jpa.repository.DictionaryAttributeRepository;
import com.example.createtemplateservice.jpa.repository.DictionaryRepository;
import com.example.createtemplateservice.jpa.service.interfaces.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryAttributeRepository dictionaryAttributeRepository;
    private final ModelMapper modelMapper;
    private final GeneralSettingClient generalSettingClient;
    private final AttributeClient attributeClient;
    private final ValueMasterClient valueMasterClient;

    @Override
    @Transactional
    public DictionaryResponse saveDictionary(DictionaryRequest dictionaryRequest) {
        Dictionary dictionary = modelMapper.map(dictionaryRequest, Dictionary.class);
        dictionary.setId(null);
        Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        // Set the parent reference in each child entity
        List<DictionaryAttribute> attributes = dictionary.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            for (DictionaryAttribute attribute : attributes) {
                attribute.setDictionary(savedDictionary);
            }
            // Save the child entities (DictionaryAttribute)
            List<DictionaryAttribute> savedAttributes = dictionaryAttributeRepository.saveAll(attributes);
            savedDictionary.setAttributes(savedAttributes);
        }
        return mapToDictionaryResponse(savedDictionary);
    }

    @Override
    public List<DictionaryResponse> getAllDictionary(String show) {
        List<Dictionary> dictionaries = dictionaryRepository.findAll();
        return dictionaries.stream()
                .sorted(Comparator.comparing(Dictionary::getId))
                .map(this::mapToDictionaryResponse)
                .toList();
    }

    @Override
    public List<DictionaryAllResponse> getAllDictionaryNmUom(String show) {
        List<Dictionary> dictionaries = dictionaryRepository.findAll();
        return dictionaries.stream()
                .sorted(Comparator.comparing(Dictionary::getId))
                .map(this::mapTODictionaryAll)
                .toList();
    }

    @Override
    public DictionaryResponse getDictionaryById(Long id, String show) throws ResourceNotFoundException {
        Dictionary dictionary = findDictionaryById(id);
        return mapToDictionaryResponse(dictionary);
    }

    @Override
    public DictionaryAllResponse getDictionaryNmUomById(Long id, String show) throws ResourceNotFoundException {
        Dictionary dictionary = findDictionaryById(id);
        return mapTODictionaryAll(dictionary);
    }

    @Override
    public DictionaryResponse updateDictionary(Long id, DictionaryRequest updateDictionaryRequest) throws ResourceNotFoundException {
        Dictionary existingDictionary = findDictionaryById(id);
        modelMapper.map(updateDictionaryRequest, existingDictionary);
        existingDictionary.setId(id);
        Dictionary updatedDictionary = dictionaryRepository.save(existingDictionary);
        // Set the parent reference in each child entity
        List<DictionaryAttribute> attributes = existingDictionary.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            for (DictionaryAttribute attribute : attributes) {
                attribute.setDictionary(updatedDictionary);
            }
            // Save the child entities (DictionaryAttribute)
            List<DictionaryAttribute> savedAttributes = dictionaryAttributeRepository.saveAll(attributes);
            updatedDictionary.setAttributes(savedAttributes);
        }
        return mapToDictionaryResponse(updatedDictionary);
    }

    @Override
    public void deleteDictionaryId(Long id) throws ResourceNotFoundException {
        Dictionary dictionary = findDictionaryById(id);
        dictionaryRepository.delete(dictionary);
    }

    @Override
    public List<String> getNounSuggestions(String noun) {
        return dictionaryRepository.findNounSuggestionsByPrefix(noun);
    }

    @Override
    public List<String> getModifiersByNoun(String noun) {
        return dictionaryRepository.findModifiersByNoun(noun);
    }

    private Dictionary findDictionaryById(Long id) throws ResourceNotFoundException {
        Optional<Dictionary> dictionary = dictionaryRepository.findById(id);
        if (dictionary.isEmpty()) {
            throw new ResourceNotFoundException("No Dictionary found with this Id");
        }
        return dictionary.get();
    }

    private DictionaryResponse mapToDictionaryResponse(Dictionary dictionary) {
        DictionaryResponse dictionaryResponse = modelMapper.map(dictionary, DictionaryResponse.class);
        List<DictionaryAttributeResponse> attributes = new ArrayList<>();
        for (DictionaryAttribute attribute : dictionary.getAttributes()) {
            DictionaryAttributeResponse attributeResponse = mapToDictionaryAttributeResponse(attribute);
            attributes.add(attributeResponse);
        }
        dictionaryResponse.setAttributes(attributes);
        return dictionaryResponse;
    }

    private DictionaryAttributeResponse mapToDictionaryAttributeResponse(DictionaryAttribute dictionaryAttribute) {
        return modelMapper.map(dictionaryAttribute, DictionaryAttributeResponse.class);
    }

    private DictionaryAllResponse mapTODictionaryAll(Dictionary dictionary) {
        DictionaryAllResponse dictionaryNmUom = modelMapper.map(dictionary, DictionaryAllResponse.class);
        List<NmUom> nmUoms = new ArrayList<>();
        for (Long id : dictionary.getNmUoms()) {
            NmUom nmUom = generalSettingClient.getNmUomById(id);
            nmUoms.add(nmUom);
        }
        dictionaryNmUom.setNmUoms(nmUoms);
        List<DictionaryAttributeAllResponse> attributes = new ArrayList<>();
        for (DictionaryAttribute attribute : dictionary.getAttributes()) {
            DictionaryAttributeAllResponse attributeResponse = mapToDictionaryAttributeAllResponse(attribute);
            attributes.add(attributeResponse);
        }
        dictionaryNmUom.setAttributes(attributes);
        return dictionaryNmUom;
    }

    private DictionaryAttributeAllResponse mapToDictionaryAttributeAllResponse(DictionaryAttribute dictionaryAttribute) {
        DictionaryAttributeAllResponse dictionaryAttributeAllResponse = modelMapper.map(dictionaryAttribute, DictionaryAttributeAllResponse.class);
        // Attribute Client
        AttributeMasterUomResponse attribute = attributeClient.getAttributeMasterById(dictionaryAttribute.getAttributeId());
        dictionaryAttributeAllResponse.setAttribute(attribute);
        // Value Master Client
        List<ValueAttributeUom> values = new ArrayList<>();
        for (Long id : dictionaryAttribute.getValueId()) {
            ValueAttributeUom value = valueMasterClient.getValueById(id, true);
            values.add(value);
        }
        dictionaryAttributeAllResponse.setValues(values);
        // General Setting Client
        List<AttributeUom> attrUoms = new ArrayList<>();
        for (Long id : dictionaryAttribute.getAttrUomId()) {
            AttributeUom attrUom = generalSettingClient.getAttributeUomById(id);
            attrUoms.add(attrUom);
        }
        dictionaryAttributeAllResponse.setAttrUoms(attrUoms);
        return dictionaryAttributeAllResponse;
    }

}
