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
import com.example.mrpdataservice.entity.LotSize;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.mapping.LotSizeMapper;
import com.example.mrpdataservice.repository.LotSizeRepo;
import com.example.mrpdataservice.request.LotSizeRequest;
import com.example.mrpdataservice.response.LotSizeResponse;
import com.example.mrpdataservice.service.LotSizeService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LotSizeServiceImpl implements LotSizeService {
	private final LotSizeRepo lotSizeRepo;
	private final ExcelFileHelper excelFileHelper;
	private final LotSizeMapper lotSizeMapper;
	private final DynamicClient dynamicClient;

	@Override
	public LotSizeResponse saveLotSize(LotSizeRequest lotSizeRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(lotSizeRequest);
		boolean exists = lotSizeRepo.existsByLotSizeCodeAndLotSizeName(lotSizeRequest.getLotSizeCode(),
				lotSizeRequest.getLotSizeName());
		if (!exists) {
			LotSize lotSize = lotSizeMapper.mapToLotSize(lotSizeRequest);
			validateDynamicFields(lotSize);
			lotSizeRepo.save(lotSize);
			return lotSizeMapper.mapToLotSizeResponse(lotSize);
		} else {
			throw new AlreadyExistsException("LotSize with this name already exists");
		}
	}

	@Override
	public LotSizeResponse getLotSizeById(Long id) throws ResourceNotFoundException {
		LotSize lotSize = this.findLotSizeById(id);
		return lotSizeMapper.mapToLotSizeResponse(lotSize);
	}

	@Override
	public List<LotSizeResponse> getAllLotSize() {
		return lotSizeRepo.findAllByOrderByIdAsc().stream().map(lotSizeMapper::mapToLotSizeResponse).toList();
	}

	@Override
	public List<LotSize> findAll() {
		return lotSizeRepo.findAllByOrderByIdAsc();
	}

	@Override
	public LotSizeResponse updateLotSize(Long id, LotSizeRequest lotSizeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.inputTitleCase(lotSizeRequest);
		String existName = lotSizeRequest.getLotSizeName();
		String existCode = lotSizeRequest.getLotSizeCode();
		boolean exists = lotSizeRepo.existsByLotSizeCodeAndLotSizeNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			LotSize existingLotSize = this.findLotSizeById(id);
			if (!existingLotSize.getLotSizeCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "LotSize Code", existingLotSize.getLotSizeCode(), existCode));
				existingLotSize.setLotSizeCode(existCode);
			}
			if (!existingLotSize.getLotSizeName().equals(existName)) {
				auditFields.add(new AuditFields(null, "LotSize Name", existingLotSize.getLotSizeName(), existName));
				existingLotSize.setLotSizeName(existName);
			}
			if (!existingLotSize.getLotSizeStatus().equals(lotSizeRequest.getLotSizeStatus())) {
				auditFields.add(new AuditFields(null, "LotSize Status", existingLotSize.getLotSizeStatus(),
						lotSizeRequest.getLotSizeStatus()));
				existingLotSize.setLotSizeStatus(lotSizeRequest.getLotSizeStatus());
			}
			if (!existingLotSize.getDynamicFields().equals(lotSizeRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : lotSizeRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingLotSize.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingLotSize.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingLotSize.updateAuditHistory(auditFields);
			lotSizeRepo.save(existingLotSize);
			return lotSizeMapper.mapToLotSizeResponse(existingLotSize);

		} else {
			throw new AlreadyExistsException("LotSize with this name already exists");
		}
	}

	@Override
	public List<LotSizeResponse> updateBulkStatusLotSizeId(List<Long> id) throws ResourceNotFoundException {
		List<LotSize> existingLotSizes = this.findAllLotSizeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingLotSizes.forEach(existingLotSize -> {
			if (existingLotSize.getLotSizeStatus() != null) {
				auditFields.add(new AuditFields(null, "LotSize Status", existingLotSize.getLotSizeStatus(),
						!existingLotSize.getLotSizeStatus()));
				existingLotSize.setLotSizeStatus(!existingLotSize.getLotSizeStatus());
			}
			existingLotSize.updateAuditHistory(auditFields);
		});
		lotSizeRepo.saveAll(existingLotSizes);
		return existingLotSizes.stream().map(lotSizeMapper::mapToLotSizeResponse).toList();
	}

	@Override
	public LotSizeResponse updateStatusUsingLotSizeId(Long id) throws ResourceNotFoundException {
		LotSize existingLotSize = this.findLotSizeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingLotSize.getLotSizeStatus() != null) {
			auditFields.add(new AuditFields(null, "LotSize Status", existingLotSize.getLotSizeStatus(),
					!existingLotSize.getLotSizeStatus()));
			existingLotSize.setLotSizeStatus(!existingLotSize.getLotSizeStatus());
		}
		existingLotSize.updateAuditHistory(auditFields);
		lotSizeRepo.save(existingLotSize);
		return lotSizeMapper.mapToLotSizeResponse(existingLotSize);
	}

	@Override
	public void deleteLotSize(Long id) throws ResourceNotFoundException {
		LotSize lotSize = this.findLotSizeById(id);
		if (lotSize != null) {
			lotSizeRepo.delete(lotSize);
		}
	}

	@Override
	public void deleteBatchLotSize(List<Long> ids) throws ResourceNotFoundException {
		List<LotSize> lotSizes = this.findAllLotSizeById(ids);
		if (!lotSizes.isEmpty()) {
			lotSizeRepo.deleteAll(lotSizes);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "LotSize";
		Class<?> clazz = LotSizeRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "LotSize_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<LotSize> dataS = excelFileHelper.readDataFromExcel(file.getInputStream(), LotSize.class);
		for (LotSize data : dataS) {
			if (!lotSizeRepo.existsByLotSizeCodeAndLotSizeName(data.getLotSizeCode(), data.getLotSizeName())) {

				this.lotSizeRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "LotSize";
		Class<?> clazz = LotSizeResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "LotSize_";
		List<LotSizeResponse> allValue = getAllLotSize();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertLotSizeListToMap(List<LotSize> lotList) {
		List<Map<String, Object>> lotSize = new ArrayList<>();

		for (LotSize lotSizes : lotList) {
			Map<String, Object> lotSizeData = new HashMap<>();
			lotSizeData.put("Id", lotSizes.getId());
			lotSizeData.put("Name", lotSizes.getLotSizeName());
			lotSizeData.put("Status", lotSizes.getLotSizeStatus());
			lotSize.add(lotSizeData);
		}
		return lotSize;
	}

	private void validateDynamicFields(LotSize lotSize) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : lotSize.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = LotSize.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private LotSize findLotSizeById(Long id) throws ResourceNotFoundException {
		return lotSizeRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("LotSize with ID " + id + " not found"));
	}

	private List<LotSize> findAllLotSizeById(List<Long> ids) throws ResourceNotFoundException {

		List<LotSize> lotSizes = lotSizeRepo.findAllById(ids);

		Set<Long> idSet = new HashSet<>(ids);

		List<LotSize> foundLotSizes = lotSizes.stream().filter(entity -> idSet.contains(entity.getId())).toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Lot Sizes with IDs " + missingIds + " not found.");
		}

		return foundLotSizes;
	}

}
