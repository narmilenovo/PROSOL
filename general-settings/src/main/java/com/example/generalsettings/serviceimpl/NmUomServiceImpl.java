package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.NmUom;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.NmUomRepo;
import com.example.generalsettings.request.NmUomRequest;
import com.example.generalsettings.response.NmUomResponse;
import com.example.generalsettings.service.NmUomService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NmUomServiceImpl implements NmUomService {
	private final NmUomRepo nmUomRepo;

	private final ModelMapper modelMapper;

	@Override
	public NmUomResponse saveNmUom(NmUomRequest nmUomRequest) throws AlreadyExistsException {
		boolean exists = nmUomRepo.existsByNmUomName(nmUomRequest.getNmUomName());
		if (!exists) {
			NmUom nmUom = modelMapper.map(nmUomRequest, NmUom.class);
			nmUomRepo.save(nmUom);
			return mapToNmUomResponse(nmUom);

		} else {
			throw new AlreadyExistsException("NmUom with this name already exists");
		}
	}

	@Override
	public NmUomResponse getNmUomById(Long id) throws ResourceNotFoundException {
		NmUom nmUom = this.findNmUomById(id);
		return mapToNmUomResponse(nmUom);
	}

	@Override
	public List<NmUom> findAll() {
		return nmUomRepo.findAllByOrderByIdAsc();
	}

	@Override
	public List<NmUomResponse> getAllNmUom() {
		List<NmUom> nmUom = nmUomRepo.findAllByOrderByIdAsc();
		return nmUom.stream().map(this::mapToNmUomResponse).toList();
	}

	@Override
	public NmUomResponse updateNmUom(Long id, NmUomRequest nmUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(nmUomRequest);
		String name = nmUomRequest.getNmUomName();
		boolean exists = nmUomRepo.existsByNmUomNameAndIdNot(name, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			NmUom existingNmUom = this.findNmUomById(id);
			if (!existingNmUom.getNmUomName().equals(nmUomRequest.getNmUomName())) {
				auditFields.add(new AuditFields(null, "NM Uom Name", existingNmUom.getNmUomName(),
						nmUomRequest.getNmUomName()));
				existingNmUom.setNmUomName(nmUomRequest.getNmUomName());
			}
			if (!existingNmUom.getNmUomStatus().equals(nmUomRequest.getNmUomStatus())) {
				auditFields.add(new AuditFields(null, "NM Uom Status", existingNmUom.getNmUomStatus(),
						nmUomRequest.getNmUomStatus()));
				existingNmUom.setNmUomStatus(nmUomRequest.getNmUomStatus());
			}
			existingNmUom.updateAuditHistory(auditFields);
			return mapToNmUomResponse(existingNmUom);
		} else {
			throw new AlreadyExistsException("NmUom with this name already exists");
		}
	}

	@Override
	public List<NmUomResponse> updateBulkStatusNmUomId(List<Long> id) throws ResourceNotFoundException {
		List<NmUom> existingNmUomList = this.findAllNmUomById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingNmUomList.forEach(existingNmUom -> {
			if (existingNmUom.getNmUomStatus() != null) {
				auditFields.add(new AuditFields(null, "NM Uom Status", existingNmUom.getNmUomStatus(),
						!existingNmUom.getNmUomStatus()));
				existingNmUom.setNmUomStatus(!existingNmUom.getNmUomStatus());
			}
			existingNmUom.updateAuditHistory(auditFields);
		});
		nmUomRepo.saveAll(existingNmUomList);
		return existingNmUomList.stream().map(this::mapToNmUomResponse).toList();
	}

	@Override
	public NmUomResponse updateStatusUsingNmUomId(Long id) throws ResourceNotFoundException {
		NmUom existingNmUom = this.findNmUomById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingNmUom.getNmUomStatus() != null) {
			auditFields.add(new AuditFields(null, "NM Uom Status", existingNmUom.getNmUomStatus(),
					!existingNmUom.getNmUomStatus()));
			existingNmUom.setNmUomStatus(!existingNmUom.getNmUomStatus());
		}
		existingNmUom.updateAuditHistory(auditFields);
		nmUomRepo.save(existingNmUom);
		return mapToNmUomResponse(existingNmUom);
	}

	@Override
	public void deleteNmUom(Long id) throws ResourceNotFoundException {
		NmUom nmUom = this.findNmUomById(id);
		nmUomRepo.deleteById(nmUom.getId());
	}

	@Override
	public void deleteBatchNmUom(List<Long> ids) throws ResourceNotFoundException {
		this.findAllNmUomById(ids);
		nmUomRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public List<Map<String, Object>> convertNmUomListToMap(List<NmUom> nmUomList) {
		List<Map<String, Object>> dataList = new ArrayList<>();

		for (NmUom unit : nmUomList) {
			Map<String, Object> data = new HashMap<>();
			data.put("Id", unit.getId());
			data.put("Name", unit.getNmUomName());
			data.put("Status", unit.getNmUomStatus());
			dataList.add(data);
		}
		return dataList;
	}

	private NmUomResponse mapToNmUomResponse(NmUom nmUom) {
		return modelMapper.map(nmUom, NmUomResponse.class);
	}

	private NmUom findNmUomById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<NmUom> nmUom = nmUomRepo.findById(id);
		if (nmUom.isEmpty()) {
			throw new ResourceNotFoundException("Nm Uom with ID " + id + " not found.");
		}
		return nmUom.get();
	}

	private List<NmUom> findAllNmUomById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<NmUom> nmUoms = nmUomRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> nmUoms.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Nm Uom with IDs " + missingIds + " not found.");
		}
		return nmUoms;
	}
}
