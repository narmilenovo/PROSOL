package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.NmUom;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.NmUomMap;
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

	private final NmUomMap nmUomMapper;

	@Override
	public NmUomResponse saveNmUom(NmUomRequest nmUomRequest) throws AlreadyExistsException {
		String nmUomName = Helpers.capitalize(nmUomRequest.getNmUomName());
		if (nmUomRepo.existsByNmUomName(nmUomName)) {
			throw new AlreadyExistsException("NmUom with this name already exists");
		}
		NmUom nmUom = nmUomMapper.mapToNmUom(nmUomRequest);
		nmUomRepo.save(nmUom);
		return nmUomMapper.mapToNmUomResponse(nmUom);

	}

	@Override
	public NmUomResponse getNmUomById(Long id) throws ResourceNotFoundException {
		NmUom nmUom = this.findNmUomById(id);
		return nmUomMapper.mapToNmUomResponse(nmUom);
	}

	@Override
	public List<NmUom> findAll() {
		return nmUomRepo.findAllByOrderByIdAsc();
	}

	@Override
	public List<NmUomResponse> getAllNmUom() {
		return nmUomRepo.findAllByOrderByIdAsc().stream().map(nmUomMapper::mapToNmUomResponse).toList();
	}

	@Override
	public NmUomResponse updateNmUom(Long id, NmUomRequest nmUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
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
			return nmUomMapper.mapToNmUomResponse(existingNmUom);
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
		return existingNmUomList.stream().map(nmUomMapper::mapToNmUomResponse).toList();
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
		return nmUomMapper.mapToNmUomResponse(existingNmUom);
	}

	@Override
	public void deleteNmUom(Long id) throws ResourceNotFoundException {
		NmUom nmUom = this.findNmUomById(id);
		if (nmUom != null) {
			nmUomRepo.delete(nmUom);
		}
	}

	@Override
	public void deleteBatchNmUom(List<Long> ids) throws ResourceNotFoundException {
		List<NmUom> nmUoms = this.findAllNmUomById(ids);
		if (!nmUoms.isEmpty()) {
			nmUomRepo.deleteAll(nmUoms);
		}
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

	private NmUom findNmUomById(Long id) throws ResourceNotFoundException {
		return nmUomRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Nm Uom with ID " + id + " not found."));
	}

	private List<NmUom> findAllNmUomById(List<Long> ids) throws ResourceNotFoundException {
		List<NmUom> nmUoms = nmUomRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);

		List<NmUom> foundNmUoms = nmUoms.stream().filter(nmUom -> idSet.contains(nmUom.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Nm Uom with IDs " + missingIds + " not found.");
		}

		return foundNmUoms;
	}

}
