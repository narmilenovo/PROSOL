package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AttributeUom;
import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.AttributeUomRepo;
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;
import com.example.generalsettings.service.AttributeUomService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttributeUomServiceImpl implements AttributeUomService {
	private final ModelMapper modelMapper;
	private final AttributeUomRepo attributeUomRepo;

	@Override
	public AttributeUomResponse saveAttributeUom(AttributeUomRequest attributeUomRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(attributeUomRequest);
		boolean exists = attributeUomRepo.existsByAttributeUomName(attributeUomRequest.getAttributeUomName());
		if (!exists) {
			AttributeUom attributeUom = modelMapper.map(attributeUomRequest, AttributeUom.class);
			attributeUomRepo.save(attributeUom);
			return mapToAttributeUomResponse(attributeUom);
		} else {
			throw new AlreadyExistsException("AttributeUom with this name already exists");
		}
	}

	@Override
	public AttributeUomResponse getAttributeUomById(Long id) throws ResourceNotFoundException {
		AttributeUom attributeUom = this.findAttributeUomById(id);
		return mapToAttributeUomResponse(attributeUom);
	}

	@Override
	public List<AttributeUomResponse> getAllAttributeUom() {
		List<AttributeUom> attributeUom = attributeUomRepo.findAllByOrderByIdAsc();
		return attributeUom.stream().map(this::mapToAttributeUomResponse).toList();
	}

	@Override
	public AttributeUomResponse updateAttributeUom(Long id, AttributeUomRequest attributeUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(attributeUomRequest);
		String name = attributeUomRequest.getAttributeUomName();
		boolean exists = attributeUomRepo.existsByAttributeUomNameAndIdNot(name, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			AttributeUom existingAttributeUom = this.findAttributeUomById(id);
			if (!existingAttributeUom.getAttributeUomName().equals(name)) {
				auditFields.add(
						new AuditFields(null, "AttributeUom Name", existingAttributeUom.getAttributeUomName(), name));
				existingAttributeUom.setAttributeUomName(name);
			}
			if (!existingAttributeUom.getAttributeUomUnit().equals(attributeUomRequest.getAttributeUomUnit())) {
				auditFields.add(new AuditFields(null, "AttributeUom Unit", existingAttributeUom.getAttributeUomUnit(),
						attributeUomRequest.getAttributeUomUnit()));
				existingAttributeUom.setAttributeUomUnit(attributeUomRequest.getAttributeUomUnit());
			}
			if (!existingAttributeUom.getAttributeUomStatus().equals(attributeUomRequest.getAttributeUomStatus())) {
				auditFields.add(new AuditFields(null, "AttributeUom Status",
						existingAttributeUom.getAttributeUomStatus(), attributeUomRequest.getAttributeUomStatus()));
				existingAttributeUom.setAttributeUomStatus(attributeUomRequest.getAttributeUomStatus());
			}
			existingAttributeUom.updateAuditHistory(auditFields);
			attributeUomRepo.save(existingAttributeUom);
			return mapToAttributeUomResponse(existingAttributeUom);
		} else {
			throw new AlreadyExistsException("AttributeUom with this name already exists");
		}
	}

	@Override
	public List<AttributeUomResponse> updateBulkStatusAttributeUomId(List<Long> id) throws ResourceNotFoundException {
		List<AttributeUom> existingAttributeUomList = this.findAllUomsById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingAttributeUomList.forEach(existingAttributeUom -> {
			if (existingAttributeUom.getAttributeUomStatus() != null) {
				auditFields.add(new AuditFields(null, "AttributeUom Status",
						existingAttributeUom.getAttributeUomStatus(), !existingAttributeUom.getAttributeUomStatus()));
				existingAttributeUom.setAttributeUomStatus(!existingAttributeUom.getAttributeUomStatus());
			}
			existingAttributeUom.updateAuditHistory(auditFields);

		});
		attributeUomRepo.saveAll(existingAttributeUomList);
		return existingAttributeUomList.stream().map(this::mapToAttributeUomResponse).toList();
	}

	@Override
	public AttributeUomResponse updateStatusUsingAttributeUomId(Long id) throws ResourceNotFoundException {
		AttributeUom existingAttributeUom = this.findAttributeUomById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingAttributeUom.getAttributeUomStatus() != null) {
			auditFields.add(new AuditFields(null, "AttributeUom Status", existingAttributeUom.getAttributeUomStatus(),
					!existingAttributeUom.getAttributeUomStatus()));
			existingAttributeUom.setAttributeUomStatus(!existingAttributeUom.getAttributeUomStatus());
		}
		existingAttributeUom.updateAuditHistory(auditFields);
		attributeUomRepo.save(existingAttributeUom);
		return mapToAttributeUomResponse(existingAttributeUom);
	}

	@Override
	public void deleteAttributeUom(Long id) throws ResourceNotFoundException {
		AttributeUom attributeUom = this.findAttributeUomById(id);
		attributeUomRepo.deleteById(attributeUom.getId());
	}

	@Override
	public void deleteBatchAttributeUom(List<Long> ids) throws ResourceNotFoundException {
		this.findAllUomsById(ids);
		attributeUomRepo.deleteAllByIdInBatch(ids);
	}

	private AttributeUomResponse mapToAttributeUomResponse(AttributeUom attributeUom) {
		return modelMapper.map(attributeUom, AttributeUomResponse.class);
	}

	private AttributeUom findAttributeUomById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<AttributeUom> attributeUom = attributeUomRepo.findById(id);
		if (attributeUom.isEmpty()) {
			throw new ResourceNotFoundException("AttributeUom with ID " + id + " not found");
		}
		return attributeUom.get();
	}

	private List<AttributeUom> findAllUomsById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<AttributeUom> uoms = attributeUomRepo.findAllById(ids);

		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> uoms.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Attribute Type with IDs " + missingIds + " not found.");
		}
		return uoms;
	}

}
