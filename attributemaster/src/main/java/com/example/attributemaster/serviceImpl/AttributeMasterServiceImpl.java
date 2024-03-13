package com.example.attributemaster.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.attributemaster.client.AttributeMasterUomResponse;
import com.example.attributemaster.client.Dynamic.DynamicServiceClient;
import com.example.attributemaster.client.GeneralSettings.AttributeUomResponse;
import com.example.attributemaster.client.GeneralSettings.SettingServiceClient;
import com.example.attributemaster.entity.AttributeMaster;
import com.example.attributemaster.entity.AuditFields;
import com.example.attributemaster.exception.AlreadyExistsException;
import com.example.attributemaster.exception.ResourceNotFoundException;
import com.example.attributemaster.mapping.AttributeMapper;
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
	private final AttributeMapper attributeMapper;
	private final SettingServiceClient serviceClient;
	private final DynamicServiceClient dynamicServiceClient;

	@Override
	public AttributeMasterResponse saveAttributeMaster(AttributeMasterRequest attributeMasterRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		String attributeName = Helpers.capitalize(attributeMasterRequest.getAttributeName());
		if (attributeMasterRepo.existsByAttributeName(attributeName)) {
			throw new AlreadyExistsException("AttributeMaster with this name already exists");
		}
		AttributeMaster attributeMaster = attributeMapper.mapToAttributeMaster(attributeMasterRequest);

		addDynamicFields(attributeMasterRequest.getDynamicFields());

		attributeMaster.setId(null);
		attributeMasterRepo.save(attributeMaster);
		return attributeMapper.mapToAttributeMasterResponse(attributeMaster);
	}

	@Override
	public AttributeMasterResponse getAttributeMasterById(Long id) throws ResourceNotFoundException {
		AttributeMaster attributeMaster = this.findAttributeMasterById(id);
		return attributeMapper.mapToAttributeMasterResponse(attributeMaster);
	}

	@Override
	public List<AttributeMasterResponse> getAllAttributeMaster() throws ResourceNotFoundException {
		List<AttributeMaster> attributeMasters = this.findAllAttributeMasters();
		return attributeMasters.stream().map(attributeMapper::mapToAttributeMasterResponse).toList();
	}

	@Override
	public AttributeMasterUomResponse getAttributeMasterUomById(Long id) throws ResourceNotFoundException {
		AttributeMaster attributeMaster = this.findAttributeMasterById(id);
		return mapAttributeMasterUomResponse(attributeMaster);
	}

	@Override
	public List<AttributeMasterUomResponse> getAllAttributeMasterUom() throws ResourceNotFoundException {
		List<AttributeMaster> attributeUomS = this.findAllAttributeMasters();
		return attributeUomS.stream().map(this::mapAttributeMasterUomResponse).toList();

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
			return attributeMapper.mapToAttributeMasterResponse(mrp);
		} else {
			throw new AlreadyExistsException("AttributeMaster with this name already exists");
		}
	}

	public void deleteAttributeMaster(Long id) throws ResourceNotFoundException {
		AttributeMaster attributeMaster = this.findAttributeMasterById(id);
		attributeMasterRepo.delete(attributeMaster);
	}

	@Override
	public void deleteBatchMaster(List<Long> ids) throws ResourceNotFoundException {
		List<AttributeMaster> attributeMasters = this.findAllById(ids);
		attributeMasterRepo.deleteAll(attributeMasters);
	}

	private void addDynamicFields(Map<String, Object> dynamicFields) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entry : dynamicFields.entrySet()) {
			String fieldName = entry.getKey();
			if (!dynamicServiceClient.checkFieldNameInForm(fieldName, AttributeMaster.class.getSimpleName())) {
				throw new ResourceNotFoundException("Field '" + fieldName + "' not found in Dynamic Field creation");
			}
		}
	}

	private List<AttributeMaster> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Map<Long, AttributeMaster> attributeMasterMap = attributeMasterRepo.findAllById(ids).stream()
				.collect(Collectors.toMap(AttributeMaster::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !attributeMasterMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("AttributeMaster with IDs " + missingIds + " not found.");
		}

		return new ArrayList<>(attributeMasterMap.values());
	}

	private AttributeMaster findAttributeMasterById(Long id) throws ResourceNotFoundException {
		return attributeMasterRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("AttributeMaster with id " + id + " not found"));

	}

	private List<AttributeMaster> findAllAttributeMasters() throws ResourceNotFoundException {
		List<AttributeMaster> attributeUomS = attributeMasterRepo.findAllByOrderByIdAsc();
		if (attributeUomS.isEmpty()) {
			throw new ResourceNotFoundException("AttributeMasters not found");
		}
		return attributeUomS;
	}

	private AttributeMasterUomResponse mapAttributeMasterUomResponse(AttributeMaster attributeMaster) {
		AttributeMasterUomResponse attributeUomResponse = attributeMapper
				.mapToAttributeMasterUomResponse(attributeMaster);
		List<AttributeUomResponse> attributeUomResponses = attributeMaster.getListUom().stream()
				.map(serviceClient::getAttributeUomById).toList();
		attributeUomResponse.setListUom(attributeUomResponses);
		return attributeUomResponse;
	}

}
