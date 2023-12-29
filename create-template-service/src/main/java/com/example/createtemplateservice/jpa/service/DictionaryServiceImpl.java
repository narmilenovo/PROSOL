package com.example.createtemplateservice.jpa.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.createtemplateservice.client.DictionaryAllResponse;
import com.example.createtemplateservice.client.DictionaryAttributeAllResponse;
import com.example.createtemplateservice.client.attributemaster.AttributeClient;
import com.example.createtemplateservice.client.attributemaster.AttributeMasterUomResponse;
import com.example.createtemplateservice.client.generalsettings.AttributeUom;
import com.example.createtemplateservice.client.generalsettings.GeneralSettingClient;
import com.example.createtemplateservice.client.generalsettings.NmUom;
import com.example.createtemplateservice.client.valuemaster.ValueAttributeUom;
import com.example.createtemplateservice.client.valuemaster.ValueMasterClient;
import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryAttributeResponse;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.entity.Dictionary;
import com.example.createtemplateservice.jpa.entity.DictionaryAttribute;
import com.example.createtemplateservice.jpa.repository.DictionaryAttributeRepository;
import com.example.createtemplateservice.jpa.repository.DictionaryRepository;
import com.example.createtemplateservice.jpa.service.interfaces.DictionaryService;
import com.example.createtemplateservice.utils.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DictionaryServiceImpl implements DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryAttributeRepository dictionaryAttributeRepository;
    private final ModelMapper modelMapper;
    private final GeneralSettingClient generalSettingClient;
    private final AttributeClient attributeClient;
    private final ValueMasterClient valueMasterClient;
    private final FileUploadUtil fileUploadUtil;

    @Override
    @Transactional
    public DictionaryResponse saveDictionary(DictionaryRequest dictionaryRequest, MultipartFile file) {
        Dictionary dictionary = modelMapper.map(dictionaryRequest, Dictionary.class);
        dictionary.setId(null);
        // Dictionary savedDictionary = dictionaryRepository.save(dictionary);
        Dictionary saveEmptyDicId = dictionaryRepository.save(dictionary);

        String fileName = fileUploadUtil.storeFile(file, saveEmptyDicId.getId());
        saveEmptyDicId.setImage(fileName);

        // Set the parent reference in each child entity
        List<DictionaryAttribute> attributes = dictionary.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            for (DictionaryAttribute attribute : attributes) {
                attribute.setDictionary(saveEmptyDicId);
            }
            // Save the child entities (DictionaryAttribute)
            List<DictionaryAttribute> savedAttributes = dictionaryAttributeRepository.saveAll(attributes);
            saveEmptyDicId.setAttributes(savedAttributes);
        }
        Dictionary savedDictionary = dictionaryRepository.save(saveEmptyDicId);
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
    public DictionaryResponse updateDictionary(Long id, DictionaryRequest updateDictionaryRequest, MultipartFile file)
            throws ResourceNotFoundException {
        Dictionary existingDictionary = findDictionaryById(id);
        modelMapper.map(updateDictionaryRequest, existingDictionary);
        existingDictionary.setId(id);
        String existingFile = existingDictionary.getImage();
        fileUploadUtil.deleteFile(existingFile, id);
        String newFile = fileUploadUtil.storeFile(file, id);
        existingDictionary.setImage(newFile);
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
        fileUploadUtil.deleteDir(dictionary.getImage(), id);
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

    private DictionaryAttributeAllResponse mapToDictionaryAttributeAllResponse(
            DictionaryAttribute dictionaryAttribute) {
        DictionaryAttributeAllResponse dictionaryAttributeAllResponse = modelMapper.map(dictionaryAttribute,
                DictionaryAttributeAllResponse.class);
        // Attribute Client
        AttributeMasterUomResponse attribute = attributeClient
                .getAttributeMasterById(dictionaryAttribute.getAttributeId());
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
