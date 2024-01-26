package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.repo.HsnRepo;
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;
import com.example.generalsettings.service.HsnService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HsnServiceImpl implements HsnService {
	private final ModelMapper modelMapper;
	private final HsnRepo hsnRepo;

	public static final String HSN_NOT_FOUND_MESSAGE = null;

	@Override
	public HsnResponse saveHsn(HsnRequest hsnRequest) throws AlreadyExistsException {

		boolean exists = hsnRepo.existsByHsnCodeAndHsnDesc(hsnRequest.getHsnCode(), hsnRequest.getHsnDesc());
		if (!exists) {
			Hsn hsn = modelMapper.map(hsnRequest, Hsn.class);
			hsnRepo.save(hsn);
			return mapToHsnResponse(hsn);
		} else {
			throw new AlreadyExistsException("Hsn with this name already exists");
		}
	}

	@Override
	public HsnResponse getHsnById(Long id) throws ResourceNotFoundException {
		Hsn hsn = this.findHsnById(id);
		return mapToHsnResponse(hsn);
	}

	@Override
	public List<HsnResponse> getAllHsn() {
		List<Hsn> hsn = hsnRepo.findAllByOrderByIdAsc();
		return hsn.stream().map(this::mapToHsnResponse).toList();
	}

	@Override
	public HsnResponse updateHsn(Long id, HsnRequest hsnRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String name = hsnRequest.getHsnDesc();
		String code = hsnRequest.getHsnCode();
		boolean exists = hsnRepo.existsByHsnCodeAndHsnDescAndIdNot(code, name, id);
		if (!exists) {
			Hsn existingHsn = this.findHsnById(id);
			modelMapper.map(hsnRequest, existingHsn);
			hsnRepo.save(existingHsn);
			return mapToHsnResponse(existingHsn);
		} else {
			throw new AlreadyExistsException("Hsn with this name already exists");
		}
	}

	@Override
	public List<HsnResponse> updateBulkStatusHsnId(List<Long> id) throws ResourceNotFoundException {
		List<Hsn> existingHsn = this.findAllHsnById(id);
		for (Hsn hsn : existingHsn) {
			hsn.setHsnStatus(!hsn.getHsnStatus());
		}
		hsnRepo.saveAll(existingHsn);
		return existingHsn.stream().map(this::mapToHsnResponse).toList();
	}

	@Override
	public HsnResponse updateStatusUsingHsnId(Long id) throws ResourceNotFoundException {
		Hsn existingHsn = this.findHsnById(id);
		existingHsn.setHsnStatus(!existingHsn.getHsnStatus());
		hsnRepo.save(existingHsn);
		return mapToHsnResponse(existingHsn);
	}

	@Override
	public void deleteHsn(Long id) throws ResourceNotFoundException {
		Hsn hsn = this.findHsnById(id);
		hsnRepo.deleteById(hsn.getId());
	}

	@Override
	public void deleteBatchHsn(List<Long> ids) throws ResourceNotFoundException {
		this.findAllHsnById(ids);
		hsnRepo.deleteAllByIdInBatch(ids);
	}

	private HsnResponse mapToHsnResponse(Hsn hsn) {
		return modelMapper.map(hsn, HsnResponse.class);
	}

	private Hsn findHsnById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<Hsn> hsn = hsnRepo.findById(id);
		if (hsn.isEmpty()) {
			throw new ResourceNotFoundException(HSN_NOT_FOUND_MESSAGE);
		}
		return hsn.get();
	}

	private List<Hsn> findAllHsnById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<Hsn> hsns = hsnRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> hsns.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Hsn with IDs " + missingIds + " not found.");
		}
		return hsns;
	}
}
