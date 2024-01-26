package com.example.mrpdataservice.serviceimpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.mrpdataservice.client.Dynamic.DynamicClient;
import com.example.mrpdataservice.entity.PlanningStrategyGrp;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
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
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String PLANNING_STRATEGY_GROUP_NOT_FOUND_MESSAGE = null;

	@Override
	public PlanningStrgyGrpResponse savePlanningStrgyGrp(PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = planningStrgyGrpRepo
				.existsByPlanningStrgGrpCodeAndPlanningStrgGrpName(planningStrgyGrpRequest.getPlanningStrgGrpCode(),
						planningStrgyGrpRequest.getPlanningStrgGrpName());
		if (!exists) {
			PlanningStrategyGrp planningStrategyGrp = modelMapper.map(planningStrgyGrpRequest,
					PlanningStrategyGrp.class);
			for (Map.Entry<String, Object> entryField : planningStrategyGrp.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = PlanningStrategyGrp.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			planningStrgyGrpRepo.save(planningStrategyGrp);
			return mapToPlanningStrgyGrpResponse(planningStrategyGrp);
		} else {
			throw new AlreadyExistsException("PlanningStrategyGrp with this name already exists");
		}
	}

	@Override
	public PlanningStrgyGrpResponse getPlanningStrgyGrpById(Long id) throws ResourceNotFoundException {
		PlanningStrategyGrp planningStrategyGrp = this.findPlanningStrategyGrpById(id);
		return mapToPlanningStrgyGrpResponse(planningStrategyGrp);
	}

	@Override
	public List<PlanningStrgyGrpResponse> getAllPlanningStrgyGrp() {
		List<PlanningStrategyGrp> planningStrategyGrp = planningStrgyGrpRepo.findAllByOrderByIdAsc();
		return planningStrategyGrp.stream().map(this::mapToPlanningStrgyGrpResponse).toList();
	}

	@Override
	public List<PlanningStrategyGrp> findAll() {
		return planningStrgyGrpRepo.findAllByOrderByIdAsc();
	}

	@Override
	public PlanningStrgyGrpResponse updatePlanningStrgyGrp(Long id, PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String exist = planningStrgyGrpRequest.getPlanningStrgGrpName();
		String existCode = planningStrgyGrpRequest.getPlanningStrgGrpCode();
		boolean exists = planningStrgyGrpRepo.existsByPlanningStrgGrpCodeAndPlanningStrgGrpNameAndIdNot(existCode,
				exist, id);
		if (!exists) {
			PlanningStrategyGrp existingPlanningStrategyGrp = this.findPlanningStrategyGrpById(id);
			modelMapper.map(planningStrgyGrpRequest, existingPlanningStrategyGrp);
			for (Map.Entry<String, Object> entryField : existingPlanningStrategyGrp.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = PlanningStrategyGrp.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			planningStrgyGrpRepo.save(existingPlanningStrategyGrp);
			return mapToPlanningStrgyGrpResponse(existingPlanningStrategyGrp);
		} else {
			throw new AlreadyExistsException("PlanningStrategyGrp with this name already exists");
		}
	}

	@Override
	public List<PlanningStrgyGrpResponse> updateBulkStatusPlanningStrgyGrpId(List<Long> id)
			throws ResourceNotFoundException {
		List<PlanningStrategyGrp> existingPlanningStrategyGrp = this.findAllStrgGrpById(id);
		for (PlanningStrategyGrp planningStrategyGrp : existingPlanningStrategyGrp) {
			planningStrategyGrp.setPlanningStrgGrpStatus(!planningStrategyGrp.getPlanningStrgGrpStatus());
		}
		planningStrgyGrpRepo.saveAll(existingPlanningStrategyGrp);
		return existingPlanningStrategyGrp.stream().map(this::mapToPlanningStrgyGrpResponse).toList();
	}

	@Override
	public PlanningStrgyGrpResponse updateStatusUsingPlanningStrgyGrpId(Long id) throws ResourceNotFoundException {
		PlanningStrategyGrp existingPlanningStrategyGrp = this.findPlanningStrategyGrpById(id);
		existingPlanningStrategyGrp
				.setPlanningStrgGrpStatus(!existingPlanningStrategyGrp.getPlanningStrgGrpStatus());
		planningStrgyGrpRepo.save(existingPlanningStrategyGrp);
		return mapToPlanningStrgyGrpResponse(existingPlanningStrategyGrp);
	}

	@Override
	public void deletePlanningStrgyGrp(Long id) throws ResourceNotFoundException {
		PlanningStrategyGrp planningStrategyGrp = this.findPlanningStrategyGrpById(id);
		planningStrgyGrpRepo.deleteById(planningStrategyGrp.getId());
	}

	@Override
	public void deleteBatchPlanningStrgyGrp(List<Long> ids) throws ResourceNotFoundException {
		this.findAllStrgGrpById(ids);
		planningStrgyGrpRepo.deleteAllByIdInBatch(ids);
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

	private PlanningStrgyGrpResponse mapToPlanningStrgyGrpResponse(PlanningStrategyGrp planningStrategyGrp) {
		return modelMapper.map(planningStrategyGrp, PlanningStrgyGrpResponse.class);
	}

	private PlanningStrategyGrp findPlanningStrategyGrpById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<PlanningStrategyGrp> planningStrategyGrp = planningStrgyGrpRepo.findById(id);
		if (planningStrategyGrp.isEmpty()) {
			throw new ResourceNotFoundException(PLANNING_STRATEGY_GROUP_NOT_FOUND_MESSAGE);
		}
		return planningStrategyGrp.get();
	}

	private List<PlanningStrategyGrp> findAllStrgGrpById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<PlanningStrategyGrp> strgGrps = planningStrgyGrpRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> strgGrps.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Planning Strategy Group with IDs " + missingIds + " not found.");
		}
		return strgGrps;
	}

}
