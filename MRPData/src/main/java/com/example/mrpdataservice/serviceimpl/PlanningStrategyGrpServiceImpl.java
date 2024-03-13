package com.example.mrpdataservice.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.client.Dynamic.DynamicClient;
import com.example.mrpdataservice.entity.AuditFields;
import com.example.mrpdataservice.entity.PlanningStrategyGrp;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.mapping.PlanningStrgyGrpMapper;
import com.example.mrpdataservice.repository.PlanningStrgyGrpRepo;
import com.example.mrpdataservice.request.PlanningStrgyGrpRequest;
import com.example.mrpdataservice.response.PlanningStrgyGrpResponse;
import com.example.mrpdataservice.service.PlanningStrgyGrpService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanningStrategyGrpServiceImpl implements PlanningStrgyGrpService {

	private final PlanningStrgyGrpRepo planningStrgyGrpRepo;
	private final ExcelFileHelper excelFileHelper;
	private final PlanningStrgyGrpMapper planningStrgyGrpMapper;
	private final DynamicClient dynamicClient;

	@Override
	public PlanningStrgyGrpResponse savePlanningStrgyGrp(PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(planningStrgyGrpRequest);
		String planningStrgGrpCode = planningStrgyGrpRequest.getPlanningStrgGrpCode();
		String planningStrgGrpName = planningStrgyGrpRequest.getPlanningStrgGrpName();
		if (planningStrgyGrpRepo.existsByPlanningStrgGrpCodeAndPlanningStrgGrpName(planningStrgGrpCode,
				planningStrgGrpName)) {
			throw new AlreadyExistsException("PlanningStrategyGrp with this name already exists");
		}
		PlanningStrategyGrp planningStrategyGrp = planningStrgyGrpMapper
				.mapToPlanningStrategyGrp(planningStrgyGrpRequest);
		validateDynamicFields(planningStrategyGrp);
		planningStrgyGrpRepo.save(planningStrategyGrp);
		return planningStrgyGrpMapper.mapToPlanningStrgyGrpResponse(planningStrategyGrp);
	}

	@Override
	public PlanningStrgyGrpResponse getPlanningStrgyGrpById(Long id) throws ResourceNotFoundException {
		PlanningStrategyGrp planningStrategyGrp = this.findPlanningStrategyGrpById(id);
		return planningStrgyGrpMapper.mapToPlanningStrgyGrpResponse(planningStrategyGrp);
	}

	@Override
	public List<PlanningStrgyGrpResponse> getAllPlanningStrgyGrp() {
		return planningStrgyGrpRepo.findAllByOrderByIdAsc().stream()
				.map(planningStrgyGrpMapper::mapToPlanningStrgyGrpResponse).toList();
	}

	@Override
	public List<PlanningStrategyGrp> findAll() {
		return planningStrgyGrpRepo.findAllByOrderByIdAsc();
	}

	@Override
	public PlanningStrgyGrpResponse updatePlanningStrgyGrp(Long id, PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(planningStrgyGrpRequest);
		String existName = planningStrgyGrpRequest.getPlanningStrgGrpName();
		String existCode = planningStrgyGrpRequest.getPlanningStrgGrpCode();
		boolean exists = planningStrgyGrpRepo.existsByPlanningStrgGrpCodeAndPlanningStrgGrpNameAndIdNot(existCode,
				existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			PlanningStrategyGrp existingPlanningStrategyGrp = this.findPlanningStrategyGrpById(id);
			if (!existingPlanningStrategyGrp.getPlanningStrgGrpCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "PlanningStrategyGrp Code",
						existingPlanningStrategyGrp.getPlanningStrgGrpCode(), existCode));
				existingPlanningStrategyGrp.setPlanningStrgGrpCode(existCode);
			}
			if (!existingPlanningStrategyGrp.getPlanningStrgGrpName().equals(existName)) {
				auditFields.add(new AuditFields(null, "PlanningStrategyGrp Name",
						existingPlanningStrategyGrp.getPlanningStrgGrpName(), existName));
				existingPlanningStrategyGrp.setPlanningStrgGrpName(existName);
			}
			if (!existingPlanningStrategyGrp.getPlanningStrgGrpStatus()
					.equals(planningStrgyGrpRequest.getPlanningStrgGrpStatus())) {
				auditFields.add(new AuditFields(null, "PlanningStrategyGrp Status",
						existingPlanningStrategyGrp.getPlanningStrgGrpStatus(),
						planningStrgyGrpRequest.getPlanningStrgGrpStatus()));
				existingPlanningStrategyGrp
						.setPlanningStrgGrpStatus(planningStrgyGrpRequest.getPlanningStrgGrpStatus()); // Clear status
			}
			if (!existingPlanningStrategyGrp.getDynamicFields().equals(planningStrgyGrpRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : planningStrgyGrpRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingPlanningStrategyGrp.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingPlanningStrategyGrp.getDynamicFields().put(fieldName, newValue); // Update the dynamic
																									// field
					}
				}
			}
			existingPlanningStrategyGrp.updateAuditHistory(auditFields);
			planningStrgyGrpRepo.save(existingPlanningStrategyGrp);
			return planningStrgyGrpMapper.mapToPlanningStrgyGrpResponse(existingPlanningStrategyGrp);
		} else {
			throw new AlreadyExistsException("PlanningStrategyGrp with this name already exists");
		}
	}

	@Override
	public List<PlanningStrgyGrpResponse> updateBulkStatusPlanningStrgyGrpId(List<Long> id)
			throws ResourceNotFoundException {
		List<PlanningStrategyGrp> existingPlanningStrategyGrps = this.findAllStrgGrpById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingPlanningStrategyGrps.forEach(existingPlanningStrategyGrp -> {
			if (existingPlanningStrategyGrp.getPlanningStrgGrpStatus() != null) {
				auditFields.add(new AuditFields(null, "PlanningStrategyGrp Status",
						existingPlanningStrategyGrp.getPlanningStrgGrpStatus(),
						!existingPlanningStrategyGrp.getPlanningStrgGrpStatus()));
				existingPlanningStrategyGrp
						.setPlanningStrgGrpStatus(!existingPlanningStrategyGrp.getPlanningStrgGrpStatus());
			}
			existingPlanningStrategyGrp.updateAuditHistory(auditFields);
		});
		planningStrgyGrpRepo.saveAll(existingPlanningStrategyGrps);
		return existingPlanningStrategyGrps.stream().map(planningStrgyGrpMapper::mapToPlanningStrgyGrpResponse)
				.toList();
	}

	@Override
	public PlanningStrgyGrpResponse updateStatusUsingPlanningStrgyGrpId(Long id) throws ResourceNotFoundException {
		PlanningStrategyGrp existingPlanningStrategyGrp = this.findPlanningStrategyGrpById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingPlanningStrategyGrp.getPlanningStrgGrpStatus() != null) {
			auditFields.add(new AuditFields(null, "PlanningStrategyGrp Status",
					existingPlanningStrategyGrp.getPlanningStrgGrpStatus(),
					!existingPlanningStrategyGrp.getPlanningStrgGrpStatus()));
			existingPlanningStrategyGrp
					.setPlanningStrgGrpStatus(!existingPlanningStrategyGrp.getPlanningStrgGrpStatus());
		}
		existingPlanningStrategyGrp.updateAuditHistory(auditFields);
		planningStrgyGrpRepo.save(existingPlanningStrategyGrp);
		return planningStrgyGrpMapper.mapToPlanningStrgyGrpResponse(existingPlanningStrategyGrp);
	}

	@Override
	public void deletePlanningStrgyGrp(Long id) throws ResourceNotFoundException {
		PlanningStrategyGrp planningStrategyGrp = this.findPlanningStrategyGrpById(id);
		if (planningStrategyGrp != null) {
			planningStrgyGrpRepo.delete(planningStrategyGrp);
		}
	}

	@Override
	public void deleteBatchPlanningStrgyGrp(List<Long> ids) throws ResourceNotFoundException {
		List<PlanningStrategyGrp> planningStrategyGrps = this.findAllStrgGrpById(ids);
		if (!planningStrategyGrps.isEmpty()) {
			planningStrgyGrpRepo.deleteAll(planningStrategyGrps);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "PlanningStrategyGrp";
		Class<?> clazz = PlanningStrategyGrp.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "PlanningStrategyGrp_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<PlanningStrategyGrp> dataS = excelFileHelper.readDataFromExcel(file.getInputStream(),
				PlanningStrategyGrp.class);
		for (PlanningStrategyGrp data : dataS) {
			if (!planningStrgyGrpRepo.existsByPlanningStrgGrpCodeAndPlanningStrgGrpName(data.getPlanningStrgGrpCode(),
					data.getPlanningStrgGrpName())) {

				this.planningStrgyGrpRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "PlanningStrategyGrp";
		Class<?> clazz = PlanningStrgyGrpResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "Department_";
		List<PlanningStrgyGrpResponse> allValue = getAllPlanningStrgyGrp();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertPlanningStrategyGrpListToMap(
			List<PlanningStrategyGrp> planningStrategyGrpList) {
		List<Map<String, Object>> planningStrategyGrp = new ArrayList<>();

		for (PlanningStrategyGrp planningStrategyGrpS : planningStrategyGrpList) {
			Map<String, Object> planningStrategyGrpData = new HashMap<>();
			planningStrategyGrpData.put("Id", planningStrategyGrpS.getId());
			planningStrategyGrpData.put("Name", planningStrategyGrpS.getPlanningStrgGrpName());
			planningStrategyGrpData.put("Status", planningStrategyGrpS.getPlanningStrgGrpStatus());
			planningStrategyGrp.add(planningStrategyGrpData);
		}
		return planningStrategyGrp;
	}

	private void validateDynamicFields(PlanningStrategyGrp planningStrategyGrp) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : planningStrategyGrp.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = PlanningStrategyGrp.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private PlanningStrategyGrp findPlanningStrategyGrpById(Long id) throws ResourceNotFoundException {
		return planningStrgyGrpRepo.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Planning Strategy Group with ID " + id + " not found."));
	}

	private List<PlanningStrategyGrp> findAllStrgGrpById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		Set<Long> idSet = new HashSet<>(ids);
		List<PlanningStrategyGrp> strgGrps = planningStrgyGrpRepo.findAllById(ids);
		List<PlanningStrategyGrp> foundStrgGrps = strgGrps.stream().filter(entity -> idSet.contains(entity.getId()))
				.toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Planning Strategy Group with IDs " + missingIds + " not found.");
		}
		return foundStrgGrps;
	}

}
