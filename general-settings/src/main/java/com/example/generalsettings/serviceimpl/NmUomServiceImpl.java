package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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

	public static final String NM_UOM_NOT_FOUND_MESSAGE = null;

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
		String name = nmUomRequest.getNmUomName();
		boolean exists = nmUomRepo.existsByNmUomNameAndIdNot(name, id);
		if (!exists) {
			NmUom existingNmUom = this.findNmUomById(id);
			modelMapper.map(nmUomRequest, existingNmUom);
			nmUomRepo.save(existingNmUom);
			return mapToNmUomResponse(existingNmUom);

		} else {
			throw new AlreadyExistsException("NmUom with this name already exists");
		}
	}

	@Override
	public List<NmUomResponse> updateBulkStatusNmUomId(List<Long> id) throws ResourceNotFoundException {
		List<NmUom> existingNmUom = this.findAllNmUomById(id);
		for (NmUom nmUom : existingNmUom) {
			nmUom.setNmUomStatus(!nmUom.getNmUomStatus());
		}
		nmUomRepo.saveAll(existingNmUom);
		return existingNmUom.stream().map(this::mapToNmUomResponse).toList();
	}

	@Override
	public NmUomResponse updateStatusUsingNmUomId(Long id) throws ResourceNotFoundException {
		NmUom existingNmUom = this.findNmUomById(id);
		existingNmUom.setNmUomStatus(!existingNmUom.getNmUomStatus());
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
			throw new ResourceNotFoundException(NM_UOM_NOT_FOUND_MESSAGE);
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
