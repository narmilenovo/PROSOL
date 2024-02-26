package com.example.attributemaster.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.attributemaster.client.AttributeMasterUomResponse;
import com.example.attributemaster.client.Dynamic.DynamicServiceClient;
import com.example.attributemaster.client.GeneralSettings.AttributeUomResponse;
import com.example.attributemaster.client.GeneralSettings.SettingServiceClient;
import com.example.attributemaster.entity.AttributeMaster;
import com.example.attributemaster.entity.AuditFields;
import com.example.attributemaster.exception.AlreadyExistsException;
import com.example.attributemaster.exception.ResourceNotFoundException;
import com.example.attributemaster.repository.AttributeMasterRepo;
import com.example.attributemaster.request.AttributeMasterRequest;
import com.example.attributemaster.response.AttributeMasterResponse;
import com.example.attributemaster.service.AttributeMasterService;
import com.example.attributemaster.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttributeMasterServiceImpl implements AttributeMasterService {
	private final AttributeMasterRepo attributeMasterRepo;
	private final ModelMapper modelMapper;
	private final SettingServiceClient serviceClient;
	private final DynamicServiceClient dynamicServiceClient;

	@Override
	public AttributeMasterResponse saveAttributeMaster(AttributeMasterRequest attributeMasterRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		String attributeName = Helpers.capitalize(attributeMasterRequest.getAttributeName());
		boolean exists = attributeMasterRepo.existsByAttributeName(attributeName);
		if (!exists) {
			AttributeMaster attributeMaster = modelMapper.map(attributeMasterRequest, AttributeMaster.class);
			for (Map.Entry<String, Object> entryField : attributeMaster.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = AttributeMaster.class.getSimpleName();
				boolean fieldExists = dynamicServiceClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			attributeMaster.setId(null);
			attributeMasterRepo.save(attributeMaster);
			return mapToAttributeMasterResponse(attributeMaster);
		} else {
			throw new AlreadyExistsException("AttributeMaster with this name already exists");
		}
	}

	@Override
	public AttributeMasterResponse getAttributeMasterById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		AttributeMaster attributeMaster = this.findAttributeMasterById(id);
		return mapToAttributeMasterResponse(attributeMaster);
	}

	@Override
	public List<AttributeMasterResponse> getAllAttributeMaster() throws ResourceNotFoundException {
		List<AttributeMaster> attributeMasters = this.findAllAttributeMasters();
		return attributeMasters.stream().map(this::mapToAttributeMasterResponse).toList();
	}

	@Override
	public AttributeMasterUomResponse getAttributeMasterUomById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		AttributeMaster attributeMaster = this.findAttributeMasterById(id);
		return mapAttributeMasterUomResponse(attributeMaster);
	}

	@Override
	public List<AttributeMasterUomResponse> getAllAttributeMasterUom() throws ResourceNotFoundException {
		List<AttributeMaster> attributeUomS = this.findAllAttributeMasters();
		List<AttributeMasterUomResponse> responseList = new ArrayList<>();
		for (AttributeMaster attributeUom : attributeUomS) {
			AttributeMasterUomResponse attributeResponse = mapAttributeMasterUomResponse(attributeUom);
			responseList.add(attributeResponse);
		}
		return responseList;
	}

	@Override
	public AttributeMasterResponse updateAttributeMaster(Long id, AttributeMasterRequest attributeMasterRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String attributeName = Helpers.capitalize(attributeMasterRequest.getAttributeName());
		boolean exists = attributeMasterRepo.existsByAttributeNameAndIdNot(attributeName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			AttributeMaster existingAttributeMaster = this.findAttributeMasterById(id);
			if (!existingAttributeMaster.getAttributeName().equals(attributeName)) {
				auditFields.add(new AuditFields(null, "Attribute Name", existingAttributeMaster.getAttributeName(),
						attributeName));
				existingAttributeMaster.setAttributeName(attributeName);
			}
			if (!existingAttributeMaster.getFieldType().equals(attributeMasterRequest.getFieldType())) {
				auditFields.add(new AuditFields(null, "Field Type", existingAttributeMaster.getFieldType(),
						attributeMasterRequest.getFieldType()));
				existingAttributeMaster.setFieldType(attributeMasterRequest.getFieldType());
			}
			if (!existingAttributeMaster.getListUom().equals(attributeMasterRequest.getListUom())) {
				auditFields.add(new AuditFields(null, "List Uom", existingAttributeMaster.getListUom(),
						attributeMasterRequest.getListUom()));
				existingAttributeMaster.setListUom(attributeMasterRequest.getListUom());
			}
			existingAttributeMaster.updateAuditHistory(auditFields);
			AttributeMaster mrp = attributeMasterRepo.save(existingAttributeMaster);
			return mapToAttributeMasterResponse(mrp);
		} else {
			throw new AlreadyExistsException("AttributeMaster with this name already exists");
		}
	}

	public void deleteAttributeMaster(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		AttributeMaster attributeMaster = this.findAttributeMasterById(id);
		attributeMasterRepo.deleteById(attributeMaster.getId());
	}

	@Override
	public void deleteBatchMaster(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<AttributeMaster> attributeMasters = this.findAllById(ids);
		attributeMasterRepo.deleteAll(attributeMasters);
	}

	private List<AttributeMaster> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<AttributeMaster> attributeMasters = attributeMasterRepo.findAllById(ids);
		List<Long> missingIds = ids.stream()
				.filter(id -> attributeMasters.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("AttributeMaster with IDs " + missingIds + " not found.");
		}
		return attributeMasters;
	}

	private AttributeMaster findAttributeMasterById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<AttributeMaster> attribute = attributeMasterRepo.findById(id);
		if (attribute.isEmpty()) {
			throw new ResourceNotFoundException("AttributeMaster with id " + id + " not found");
		}
		return attribute.get();
	}

	private List<AttributeMaster> findAllAttributeMasters() throws ResourceNotFoundException {
		List<AttributeMaster> attributeUomS = attributeMasterRepo.findAllByOrderByIdAsc();
		if (attributeUomS.isEmpty() || attributeUomS == null) {
			throw new ResourceNotFoundException("AttributeMasters not found");
		}
		return attributeUomS;
	}

	private AttributeMasterResponse mapToAttributeMasterResponse(AttributeMaster attributeMaster) {
		return modelMapper.map(attributeMaster, AttributeMasterResponse.class);
	}

	private AttributeMasterUomResponse mapAttributeMasterUomResponse(AttributeMaster attributeMaster)
			throws ResourceNotFoundException {
		AttributeMasterUomResponse attributeUomResponse = modelMapper.map(attributeMaster,
				AttributeMasterUomResponse.class);
		List<AttributeUomResponse> attributeUomResponses = new ArrayList<>();
		for (Long id : attributeMaster.getListUom()) {
			if (serviceClient == null) {
				throw new IllegalStateException("AttributeUomClient is not initialized");
			}
			AttributeUomResponse attributeUomResponse1 = serviceClient.getAttributeUomById(id);
			attributeUomResponses.add(attributeUomResponse1);
		}
		attributeUomResponse.setListUom(attributeUomResponses);
		return attributeUomResponse;
	}

}
