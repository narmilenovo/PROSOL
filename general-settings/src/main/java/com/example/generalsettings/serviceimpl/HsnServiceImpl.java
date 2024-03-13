package com.example.generalsettings.serviceimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.generalsettings.entity.AuditFields;
import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.mapping.HsnMapper;
import com.example.generalsettings.repo.HsnRepo;
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;
import com.example.generalsettings.service.HsnService;
import com.example.generalsettings.util.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HsnServiceImpl implements HsnService {
	private final HsnMapper hsnMapper;
	private final HsnRepo hsnRepo;

	@Override
	public HsnResponse saveHsn(HsnRequest hsnRequest) throws AlreadyExistsException {
		Helpers.inputTitleCase(hsnRequest);
		String hsnCode = hsnRequest.getHsnCode();
		String hsnDesc = hsnRequest.getHsnDesc();
		if (hsnRepo.existsByHsnCodeAndHsnDesc(hsnCode, hsnDesc)) {
			throw new AlreadyExistsException("Hsn with this name already exists");
		}
		Hsn hsn = hsnMapper.mapToHsn(hsnRequest);
		hsnRepo.save(hsn);
		return hsnMapper.mapToHsnResponse(hsn);
	}

	@Override
	public HsnResponse getHsnById(Long id) throws ResourceNotFoundException {
		Hsn hsn = this.findHsnById(id);
		return hsnMapper.mapToHsnResponse(hsn);
	}

	@Override
	public List<HsnResponse> getAllHsn() {
		return hsnRepo.findAllByOrderByIdAsc().stream().map(hsnMapper::mapToHsnResponse).toList();
	}

	@Override
	public HsnResponse updateHsn(Long id, HsnRequest hsnRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(hsnRequest);
		String description = hsnRequest.getHsnDesc();
		String code = hsnRequest.getHsnCode();
		boolean exists = hsnRepo.existsByHsnCodeAndHsnDescAndIdNot(code, description, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			Hsn existingHsn = this.findHsnById(id);
			if (!existingHsn.getHsnCode().equals(code)) {
				auditFields.add(new AuditFields(null, "Hsn Code", existingHsn.getHsnCode(), code));
				existingHsn.setHsnCode(code);
			}
			if (!existingHsn.getHsnDesc().equals(description)) {
				auditFields.add(new AuditFields(null, "Hsn Description", existingHsn.getHsnDesc(), description));
				existingHsn.setHsnDesc(description);
			}
			if (!existingHsn.getHsnStatus().equals(hsnRequest.getHsnStatus())) {
				auditFields.add(
						new AuditFields(null, "Hsn Status", existingHsn.getHsnStatus(), hsnRequest.getHsnStatus()));
				existingHsn.setHsnStatus(hsnRequest.getHsnStatus());
			}
			existingHsn.updateAuditHistory(auditFields);
			hsnRepo.save(existingHsn);
			return hsnMapper.mapToHsnResponse(existingHsn);
		} else {
			throw new AlreadyExistsException("Hsn with this name already exists");
		}
	}

	@Override
	public List<HsnResponse> updateBulkStatusHsnId(List<Long> id) throws ResourceNotFoundException {
		List<Hsn> existingHsnList = this.findAllHsnById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingHsnList.forEach(existingHsn -> {
			if (existingHsn.getHsnStatus() != null) {
				auditFields.add(
						new AuditFields(null, "Hsn Status", existingHsn.getHsnStatus(), !existingHsn.getHsnStatus()));
				existingHsn.setHsnStatus(!existingHsn.getHsnStatus());
			}
			existingHsn.updateAuditHistory(auditFields);

		});
		hsnRepo.saveAll(existingHsnList);
		return existingHsnList.stream().map(hsnMapper::mapToHsnResponse).toList();
	}

	@Override
	public HsnResponse updateStatusUsingHsnId(Long id) throws ResourceNotFoundException {
		Hsn existingHsn = this.findHsnById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingHsn.getHsnStatus() != null) {
			auditFields
					.add(new AuditFields(null, "Hsn Status", existingHsn.getHsnStatus(), !existingHsn.getHsnStatus()));
			existingHsn.setHsnStatus(!existingHsn.getHsnStatus());
		}
		existingHsn.updateAuditHistory(auditFields);
		hsnRepo.save(existingHsn);
		return hsnMapper.mapToHsnResponse(existingHsn);
	}

	@Override
	public void deleteHsn(Long id) throws ResourceNotFoundException {
		Hsn hsn = this.findHsnById(id);
		if (hsn != null) {
			hsnRepo.delete(hsn);
		}
	}

	@Override
	public void deleteBatchHsn(List<Long> ids) throws ResourceNotFoundException {
		List<Hsn> hsns = this.findAllHsnById(ids);
		if (!hsns.isEmpty()) {
			hsnRepo.deleteAll(hsns);
		}
	}

	private Hsn findHsnById(Long id) throws ResourceNotFoundException {
		return hsnRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Hsn with" + id + " not found !!!"));
	}

	private List<Hsn> findAllHsnById(List<Long> ids) throws ResourceNotFoundException {

		List<Hsn> hsns = hsnRepo.findAllById(ids);
		Set<Long> idSet = new HashSet<>(ids);
		List<Hsn> foundHsns = hsns.stream().filter(hsn -> idSet.contains(hsn.getId())).toList();
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();
		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Hsn with IDs " + missingIds + " not found.");
		}

		return foundHsns;
	}

}
