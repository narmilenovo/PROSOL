package com.example.valueservice.service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.valueservice.client.ValueAttributeUom;
import com.example.valueservice.client.Dynamic.DynamicClient;
import com.example.valueservice.client.GeneralSetting.AttributeUom;
import com.example.valueservice.client.GeneralSetting.SettingClient;
import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.entity.ValueMaster;
import com.example.valueservice.exceptions.ExcelFileException;
import com.example.valueservice.exceptions.ResourceNotFoundException;
import com.example.valueservice.repository.ValueMasterRepository;
import com.example.valueservice.service.interfaces.ValueMasterService;
import com.example.valueservice.utils.ExcelFileHelper;
import com.example.valueservice.utils.Helpers;
import com.example.valueservice.utils.PdfFileHelper;
import com.itextpdf.text.DocumentException;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValueMasterServiceImpl implements ValueMasterService {

	private final ValueMasterRepository valueMasterRepository;
	private final ModelMapper modelMapper;
	private final SettingClient settingClient;
	private final Tracer tracer;
	private final DynamicClient dynamicClient;
	@Lazy
	private final ExcelFileHelper excelFileHelper;
	@Lazy
	private final PdfFileHelper pdfFileHelper;

	private static final String VALUE_PREFIX = "ValueMaster_";

	@Override
	public ValueMasterResponse saveValue(ValueMasterRequest valueMasterRequest) throws ResourceNotFoundException {
		ValueMaster valueMaster = modelMapper.map(valueMasterRequest, ValueMaster.class);
		for (Map.Entry<String, Object> entryField : valueMaster.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = ValueMaster.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
		valueMaster.setId(null);
		ValueMaster savedValue = valueMasterRepository.save(valueMaster);
		return mapToValueMasterResponse(savedValue);
	}

	@Override
	@Cacheable(value = "valueMaster", key = "#id")
	public ValueMasterResponse getValueById(Long id) throws ResourceNotFoundException {
		ValueMaster valueMaster = findValueById(id);
		log.info("Fetch Data from Db {}", valueMaster);
		return mapToValueMasterResponse(valueMaster);
	}

	@Override
	@Cacheable(value = "valueAttributeUom", key = "#id")
	public ValueAttributeUom getValueAttributeUomById(Long id) throws ResourceNotFoundException {
		ValueMaster valueMaster = findValueById(id);
		log.info("Fetch Data from Db {}", valueMaster);
		return mapToValueAttributeUom(valueMaster);
	}

	@Override
	@CachePut(value = "valueMaster")
	public List<ValueMasterResponse> getAllValue(boolean attributeUom) throws ResourceNotFoundException {
		List<ValueMaster> allValues = this.findAllValues();
		log.info("Fetch Data from Db {}", allValues);
		return allValues.stream().sorted(Comparator.comparing(ValueMaster::getId)).map(this::mapToValueMasterResponse)
				.toList();
	}

	@Override
	@CachePut(value = "valueAttributeUom")
	public List<ValueAttributeUom> getAllValueAttributeUom() throws ResourceNotFoundException {
		List<ValueMaster> allValues = this.findAllValues();
		log.info("Fetch Data from Db {}", allValues);
		return allValues.stream().sorted(Comparator.comparing(ValueMaster::getId)).map(this::mapToValueAttributeUom)
				.toList();
	}

	@Override
	@CachePut(value = "valueMaster", key = "#id")
	public ValueMasterResponse updateValue(Long id, ValueMasterRequest updateValueMasterRequest)
			throws ResourceNotFoundException {
		try {
			Helpers.validateId(id);
			ValueMaster existingValueMaster = findValueById(id);
			modelMapper.map(updateValueMasterRequest, existingValueMaster);
			for (Map.Entry<String, Object> entryField : existingValueMaster.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ValueMaster.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			existingValueMaster.setId(id);
			ValueMaster updatedValueMaster = valueMasterRepository.save(existingValueMaster);
			log.info("Update Data from Db {}", updatedValueMaster);
			return mapToValueMasterResponse(updatedValueMaster);
		} catch (Exception e) {
			// Log the exception or handle it as appropriate for your application.
			log.error("Error updating cache for ValueMaster with ID {}: {}", id, e.getMessage());
			throw new RuntimeException("Error updating ValueMaster", e); // You can throw a more specific exception if
																			// needed.
		}
	}

	@Override
	@CacheEvict(value = { "valueMaster", "valueAttributeUom" }, allEntries = true)
	public void deleteValueId(Long id) throws ResourceNotFoundException {
		ValueMaster valueMaster = findValueById(id);
		valueMasterRepository.deleteById(valueMaster.getId());
	}

	@Override
	public void deleteBatchValue(List<Long> ids) throws ResourceNotFoundException {
		this.findAllValuesById(ids);
		valueMasterRepository.deleteAllByIdInBatch(ids);

	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ValueMaster";
		Class<?> clazz = ValueMasterRequest.class;
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = VALUE_PREFIX;
		excelFileHelper.exportTemplate(response, sheetName, clazz, contentType, extension, prefix);
	}

	@Override
	public void uploadData(MultipartFile file) throws IOException, ExcelFileException {
		List<ValueMasterRequest> fromExcel = excelFileHelper.readDataFromExcel(file.getInputStream(),
				ValueMasterRequest.class);
		List<ValueMaster> valueMasters = modelMapper.map(fromExcel, new TypeToken<List<ValueMaster>>() {
		}.getType());
		valueMasters.forEach(valueMaster -> valueMaster.setId(null));
		valueMasterRepository.saveAll(valueMasters);
	}

	@Override
	public void downloadAllData(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException {
		String sheetName = "ValueMaster";
		Class<?> clazz = ValueMasterResponse.class;
		String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = VALUE_PREFIX;
		List<ValueMasterResponse> allValues = getAllValue(false);
		excelFileHelper.exportData(response, sheetName, clazz, contentType, extension, prefix, allValues);
	}

	@Override
	public void exportPdf(HttpServletResponse response)
			throws IOException, IllegalAccessException, ExcelFileException, DocumentException,
			ResourceNotFoundException {
		String headerName = "list Of values";
		Class<?> clazz = ValueMasterResponse.class;
		String contentType = "application/pdf";
		String extension = ".pdf";
		String prefix = VALUE_PREFIX;
		List<ValueMasterResponse> allValues = getAllValue(false);
		pdfFileHelper.export(response, headerName, clazz, contentType, extension, prefix, allValues);
	}

	private ValueMaster findValueById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ValueMaster> valueMaster = valueMasterRepository.findById(id);
		return valueMaster.orElseThrow(() -> new ResourceNotFoundException("Value Master with this ID Not found"));
	}

	private List<ValueMaster> findAllValues() throws ResourceNotFoundException {
		List<ValueMaster> valueMasters = valueMasterRepository.findAll();
		if (valueMasters.isEmpty()) {
			throw new ResourceNotFoundException("Value Master Data is Empty !!!");
		}
		return valueMasters;
	}

	private List<ValueMaster> findAllValuesById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ValueMaster> valueMasters = valueMasterRepository.findAllById(ids);
		List<Long> missingIds = ids.stream()
				.filter(id -> valueMasters.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("AttributeMaster with IDs " + missingIds + " not found.");
		}
		return valueMasters;
	}

	private ValueMasterResponse mapToValueMasterResponse(ValueMaster valueMaster) {
		return modelMapper.map(valueMaster, ValueMasterResponse.class);
	}

	private ValueAttributeUom mapToValueAttributeUom(ValueMaster valueMaster) {
		ValueAttributeUom valueAttributeUom = modelMapper.map(valueMaster, ValueAttributeUom.class);

		Span attributeUomLookUp = tracer.nextSpan().name("AttributeUomLookUp");

		try (Tracer.SpanInScope aUom = tracer.withSpan(attributeUomLookUp.start())) {
			if (settingClient == null) {
				throw new IllegalStateException("Setting Client is not initiated");
			}
			AttributeUom abbreviationUnit = settingClient.getAttributeUomById(valueMaster.getAbbreviationUnit());
			AttributeUom equivalentUnit = settingClient.getAttributeUomById(valueMaster.getEquivalentUnit());
			valueAttributeUom.setAbbreviationUnit(abbreviationUnit);
			valueAttributeUom.setEquivalentUnit(equivalentUnit);
			return valueAttributeUom;
		} finally {
			attributeUomLookUp.end();
		}

	}

}
