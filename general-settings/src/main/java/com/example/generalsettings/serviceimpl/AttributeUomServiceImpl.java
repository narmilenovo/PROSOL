package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AttributeUom;
import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.AttributeUomMapper;
import com.example.generalsettings.repo.AttributeUomRepo;
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;
import com.example.generalsettings.service.AttributeUomService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttributeUomServiceImpl implements AttributeUomService {
	private final AttributeUomMapper attributeUomMapper;
	private final AttributeUomRepo attributeUomRepo;

	@Override
	public AttributeUomResponse saveAttributeUom(AttributeUomRequest attributeUomRequest)
			throws AlreadyExistsException {
		Helpers.inputTitleCase(attributeUomRequest);
		String attributeUomName = attributeUomRequest.getAttributeUomName();
		if (attributeUomRepo.existsByAttributeUomName(attributeUomName)) {
			throw new AlreadyExistsException("AttributeUom with this name already exists");
		}
		AttributeUom attributeUom = attributeUomMapper.mapToAttributeUom(attributeUomRequest);
		attributeUomRepo.save(attributeUom);
		return attributeUomMapper.mapToAttributeUomResponse(attributeUom);
	}

	@Override
	public AttributeUomResponse getAttributeUomById(Long id) throws ResourceNotFoundException {
		AttributeUom attributeUom = this.findAttributeUomById(id);
		return attributeUomMapper.mapToAttributeUomResponse(attributeUom);
	}

	@Override
	public List<AttributeUomResponse> getAllAttributeUom() {
		return attributeUomRepo.findAllByOrderByIdAsc().stream().map(attributeUomMapper::mapToAttributeUomResponse)
				.toList();
	}

	@Override
	public AttributeUomResponse updateAttributeUom(Long id, AttributeUomRequest attributeUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
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
			return attributeUomMapper.mapToAttributeUomResponse(existingAttributeUom);
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
		return existingAttributeUomList.stream().map(attributeUomMapper::mapToAttributeUomResponse).toList();
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
		return attributeUomMapper.mapToAttributeUomResponse(existingAttributeUom);
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

	private AttributeUom findAttributeUomById(Long id) throws ResourceNotFoundException {
		return attributeUomRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("AttributeUom with ID " + id + " not found"));
	}

	private List<AttributeUom> findAllUomsById(List<Long> ids) throws ResourceNotFoundException {
		List<AttributeUom> uoms = attributeUomRepo.findAllById(ids);
		Set<Long> idSet = new HashSet<>(ids);
		List<AttributeUom> foundUoms = uoms.stream().filter(uom -> idSet.contains(uom.getId())).toList();
		if (foundUoms.size() != ids.size()) {
			List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();
			throw new ResourceNotFoundException("Attribute Uom with IDs " + missingIds + " not found.");
		}

		return uoms;
	}

}
