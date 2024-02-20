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
import com.example.createtemplateservice.exceptions.ResourceFoundException;
import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryAttributeResponse;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.entity.AuditFields;
import com.example.createtemplateservice.jpa.entity.Dictionary;
import com.example.createtemplateservice.jpa.entity.DictionaryAttribute;
import com.example.createtemplateservice.jpa.repository.DictionaryAttributeRepository;
import com.example.createtemplateservice.jpa.repository.DictionaryRepository;
import com.example.createtemplateservice.jpa.service.interfaces.DictionaryService;
import com.example.createtemplateservice.utils.FileUploadUtil;
import com.example.createtemplateservice.utils.Helpers;

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
	public DictionaryResponse saveDictionary(DictionaryRequest dictionaryRequest, MultipartFile file)
			throws ResourceFoundException {
		Helpers.inputTitleCase(dictionaryRequest);
		String noun = dictionaryRequest.getNoun();
		String modifier = dictionaryRequest.getModifier();

		boolean exists = dictionaryRepository.existsByNounAndModifier(noun, modifier);

		if (!exists) {
			Dictionary dictionary = modelMapper.map(dictionaryRequest, Dictionary.class);
			dictionary.setId(null);
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
		} else {
			throw new ResourceFoundException("Record with noun and modifer already exists !!!");
		}
	}

	@Override
	public DictionaryResponse getDictionaryById(Long id, String show) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Dictionary dictionary = findDictionaryById(id);
		return mapToDictionaryResponse(dictionary);
	}

	@Override
	public DictionaryAllResponse getDictionaryNmUomById(Long id, String show) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Dictionary dictionary = findDictionaryById(id);
		return mapToDictionaryAll(dictionary);
	}

	@Override
	public List<DictionaryResponse> getAllDictionary(String show) throws ResourceNotFoundException {
		List<Dictionary> dictionaries = this.findAll();
		return dictionaries.stream().sorted(Comparator.comparing(Dictionary::getId)).map(this::mapToDictionaryResponse)
				.toList();
	}

	@Override
	public List<DictionaryAllResponse> getAllDictionaryNmUom(String show) throws ResourceNotFoundException {
		List<Dictionary> dictionaries = this.findAll();
		return dictionaries.stream().sorted(Comparator.comparing(Dictionary::getId)).map(this::mapToDictionaryAll)
				.toList();
	}

	@Override
	public List<String> getNounSuggestions(String noun) {
		return dictionaryRepository.findNounSuggestionsByPrefix(noun);
	}

	@Override
	public List<String> getModifiersByNoun(String noun) {
		return dictionaryRepository.findModifiersByNoun(noun);
	}

	@Override
	public DictionaryResponse getRecordByNounAndModifer(String noun, String modifier) {
		Dictionary dictionary = dictionaryRepository.findByNounAndModifier(noun, modifier);
		return this.mapToDictionaryResponse(dictionary);
	}

	@Override
	public DictionaryResponse updateDictionary(Long id, DictionaryRequest updateDictionaryRequest, MultipartFile file)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateDictionaryRequest);
		String noun = updateDictionaryRequest.getNoun();
		String modifier = updateDictionaryRequest.getModifier();
		boolean exist = dictionaryRepository.existsByNounAndModifierAndIdNot(noun, modifier, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exist) {
			Dictionary existingDictionary = findDictionaryById(id);

			if (!existingDictionary.getNoun().equals(noun)) {
				auditFields.add(new AuditFields(null, "Noun", existingDictionary.getNoun(), noun));
				existingDictionary.setNoun(noun);
			}
			if (!existingDictionary.getNounSynonyms().equals(updateDictionaryRequest.getNounSynonyms())) {
				auditFields.add(new AuditFields(null, "Noun Synonyms", existingDictionary.getNounSynonyms(),
						updateDictionaryRequest.getNounSynonyms()));
				existingDictionary.setNounSynonyms(updateDictionaryRequest.getNounSynonyms());
			}
			if (!existingDictionary.getNmAbbreviation().equals(updateDictionaryRequest.getNmAbbreviation())) {
				auditFields.add(new AuditFields(null, "Noun Modifier Abbreviation",
						existingDictionary.getNmAbbreviation(), updateDictionaryRequest.getNmAbbreviation()));
				existingDictionary.setNmAbbreviation(updateDictionaryRequest.getNmAbbreviation());
			}
			if (!existingDictionary.getModifier().equals(modifier)) {
				auditFields.add(new AuditFields(null, "Modifier", existingDictionary.getModifier(), modifier));
				existingDictionary.setModifier(modifier);
			}
			if (!existingDictionary.getModifierSynonyms().equals(updateDictionaryRequest.getModifierSynonyms())) {
				auditFields.add(new AuditFields(null, "Modifier Synonyms", existingDictionary.getModifierSynonyms(),
						updateDictionaryRequest.getModifierSynonyms()));
				existingDictionary.setModifierSynonyms(updateDictionaryRequest.getModifierSynonyms());
			}
			if (!existingDictionary.getNmDefinition().equals(updateDictionaryRequest.getNmDefinition())) {
				auditFields.add(new AuditFields(null, "Noun Modifier Definition", existingDictionary.getNmDefinition(),
						updateDictionaryRequest.getNmDefinition()));
				existingDictionary.setNmDefinition(updateDictionaryRequest.getNmDefinition());
			}
			if (!existingDictionary.getType().equals(updateDictionaryRequest.getType())) {
				auditFields.add(
						new AuditFields(null, "Type", existingDictionary.getType(), updateDictionaryRequest.getType()));
				existingDictionary.setType(updateDictionaryRequest.getType());
			}
			if (!existingDictionary.getSimilarSearchItems().equals(updateDictionaryRequest.getSimilarSearchItems())) {
				auditFields.add(new AuditFields(null, "Similar Search Items",
						existingDictionary.getSimilarSearchItems(), updateDictionaryRequest.getSimilarSearchItems()));
				existingDictionary.setSimilarSearchItems(updateDictionaryRequest.getSimilarSearchItems());
			}
			if (!existingDictionary.getNmUoms().equals(updateDictionaryRequest.getNmUoms())) {
				auditFields.add(new AuditFields(null, "Noun Modifier Uoms", existingDictionary.getNmUoms(),
						updateDictionaryRequest.getNmUoms()));
				existingDictionary.setNmUoms(updateDictionaryRequest.getNmUoms());
			}
			String existingFile = existingDictionary.getImage();
			if (!existingFile.equals(updateDictionaryRequest.getImage())) {
				auditFields.add(new AuditFields(null, "Image", existingFile, updateDictionaryRequest.getImage()));
				fileUploadUtil.deleteFile(existingFile, id);
				String newFile = fileUploadUtil.storeFile(file, id);
				existingDictionary.setImage(newFile);
			}
			existingDictionary.updateAuditHistory(auditFields);
			Dictionary updatedDictionary = dictionaryRepository.save(existingDictionary);
			// Set the parent reference in each child entity
			List<DictionaryAttribute> attributes = existingDictionary.getAttributes();
			if (!attributes.equals(updateDictionaryRequest.getAttributes())) {
				auditFields.add(new AuditFields(null, "Attributes", attributes, updatedDictionary.getAttributes()));
				attributes.forEach(attribute -> attribute.setDictionary(updatedDictionary));
				// Save the child entities (DictionaryAttribute)
				List<DictionaryAttribute> savedAttributes = dictionaryAttributeRepository.saveAll(attributes);
				updatedDictionary.setAttributes(savedAttributes);
			}
			return mapToDictionaryResponse(updatedDictionary);
		} else {
			throw new ResourceFoundException("Record with noun and modifier already exists in db !!!");
		}
	}

	@Override
	public void deleteDictionaryId(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Dictionary dictionary = findDictionaryById(id);
		fileUploadUtil.deleteDir(dictionary.getImage(), id);
		dictionaryRepository.delete(dictionary);
	}

	@Override
	public void deleteBatchDictionary(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<Dictionary> attributeMasters = dictionaryRepository.findAllById(ids);

		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> attributeMasters.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Dictionaries with IDs " + missingIds + " not found.");
		}
		dictionaryRepository.deleteAllById(ids);
	}

	private Dictionary findDictionaryById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<Dictionary> dictionary = dictionaryRepository.findById(id);
		if (dictionary.isEmpty()) {
			throw new ResourceNotFoundException("No Dictionary found with this Id");
		}
		return dictionary.get();
	}

	private List<Dictionary> findAll() throws ResourceNotFoundException {
		List<Dictionary> dictionaries = dictionaryRepository.findAll();
		if (dictionaries.isEmpty()) {
			throw new ResourceNotFoundException("Dictionaries is Empty");
		}
		return dictionaries;
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

	private DictionaryAllResponse mapToDictionaryAll(Dictionary dictionary) {
		DictionaryAllResponse dictionaryNmUom = modelMapper.map(dictionary, DictionaryAllResponse.class);
		List<NmUom> nmUoms = new ArrayList<>();
		for (Long id : dictionary.getNmUoms()) {
			if (generalSettingClient == null) {
				throw new IllegalStateException("General setting Client is not initialized");
			}
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
		if (attributeClient == null) {
			throw new IllegalStateException("Attribute Client is not initialized");
		}
		AttributeMasterUomResponse attribute = attributeClient
				.getAttributeMasterById(dictionaryAttribute.getAttributeId());
		dictionaryAttributeAllResponse.setAttribute(attribute);
		// Value Master Client
		List<ValueAttributeUom> values = new ArrayList<>();
		for (Long id : dictionaryAttribute.getValueId()) {
			if (valueMasterClient == null) {
				throw new IllegalStateException("Value master client is not initialized");
			}
			ValueAttributeUom value = valueMasterClient.getValueById(id, true);
			values.add(value);
		}
		dictionaryAttributeAllResponse.setValues(values);
		// General Setting Client
		List<AttributeUom> attrUoms = new ArrayList<>();
		for (Long id : dictionaryAttribute.getAttrUomId()) {
			if (generalSettingClient == null) {
				throw new IllegalStateException("General setting Client is not initialized");
			}
			AttributeUom attrUom = generalSettingClient.getAttributeUomById(id);
			attrUoms.add(attrUom);
		}
		dictionaryAttributeAllResponse.setAttrUoms(attrUoms);
		return dictionaryAttributeAllResponse;
	}

}
