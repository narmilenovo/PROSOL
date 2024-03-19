package com.example.dynamic.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dynamic.client.AttributeServiceClient;
import com.example.dynamic.client.GeneralServiceClient;
import com.example.dynamic.client.MrpServiceClient;
import com.example.dynamic.client.PlantServiceClient;
import com.example.dynamic.client.SalesServiceClient;
import com.example.dynamic.client.SettingServiceClient;
import com.example.dynamic.client.ValueServiceClient;
import com.example.dynamic.client.VendorServiceClient;
import com.example.dynamic.dto.request.DropDownRequest;
import com.example.dynamic.dto.request.FormFieldRequest;
import com.example.dynamic.dto.response.FormFieldResponse;
import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.Form;
import com.example.dynamic.entity.FormField;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.mapping.FormFieldMapper;
import com.example.dynamic.repository.FormFieldRepository;
import com.example.dynamic.repository.FormRepository;
import com.example.dynamic.service.interfaces.FormFieldService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormFieldServiceImpl implements FormFieldService {

	private final FormFieldRepository fieldRepository;
	private final FormRepository formRepository;
	private final FormFieldMapper formFieldMapper;

	private final MrpServiceClient mrpServiceClient;
	private final PlantServiceClient plantServiceClient;
	private final GeneralServiceClient generalServiceClient;
	private final SalesServiceClient salesServiceClient;
	private final VendorServiceClient vendorServiceClient;
	private final SettingServiceClient settingServiceClient;
	private final AttributeServiceClient attributeServiceClient;
	private final ValueServiceClient valueServiceClient;

	public static final String TEXT_FIELD_TYPE = "textField";
	public static final String TEXT_AREA_TYPE = "textArea";
	public static final String DATE_TYPE = "date";
	public static final String OBJECT_TYPE = "object";
	public static final String CHECKBOX_TYPE = "radioButton";
	public static final String DROPDOWN_TYPE = "dropDown";
	public static final String FILE_TYPE = "fileUpload";
	private static final Map<Class<?>[], String> REACT_TYPES;

	static {
		REACT_TYPES = new HashMap<>();
		REACT_TYPES.put(new Class[] { String.class, Long.class, Integer.class, Float.class, Double.class },
				TEXT_FIELD_TYPE);
		REACT_TYPES.put(new Class[] { String.class }, TEXT_AREA_TYPE);
		REACT_TYPES.put(new Class[] { Date.class, DateTime.class }, DATE_TYPE);
		REACT_TYPES.put(new Class[] { Boolean.class }, CHECKBOX_TYPE);
		REACT_TYPES.put(new Class[] { Enum.class, List.class }, DROPDOWN_TYPE);
		REACT_TYPES.put(new Class[] { Object.class }, OBJECT_TYPE);
	}

	private String setFieldConversion(String dataType) {
		for (Map.Entry<Class<?>[], String> entry : REACT_TYPES.entrySet()) {
			String value = entry.getValue();
			if (value.equalsIgnoreCase(dataType)) {
				return value;
			}
			if (dataType.equalsIgnoreCase(FILE_TYPE)) {
				return FILE_TYPE;
			}
		}
		return OBJECT_TYPE;
	}

	@Override
	@Transactional
	public FormFieldResponse createDynamicField(String formName, FormFieldRequest fieldRequest)
			throws ResourceFoundException {
		FormField formField = formFieldMapper.mapToFormField(fieldRequest);
		Form form = getOrCreateForm(formName);
		formField.setForm(form);
		conversionEquals(formField);

		List<DropDown> dropDownValues = mapDropDownValues(fieldRequest.getDropDowns(), formField);
		formField.setDropDowns(dropDownValues);
		if (fieldRepository.existsByFieldNameAndForm_FormName(fieldRequest.getFieldName(), formName)) {
			throw new ResourceFoundException(
					"Field with name '" + fieldRequest.getFieldName() + "' already exists in form '" + formName + "'.");
		}
		FormField savedFormField = fieldRepository.save(formField);
		return formFieldMapper.mapToFieldResponse(savedFormField);
	}

	@Override
	public FormFieldResponse getDynamicFieldById(@NonNull Long id) throws ResourceNotFoundException {
		FormField formField = this.getById(id);
		return formFieldMapper.mapToFieldResponse(formField);
	}

	@Override
	public List<FormFieldResponse> getAllDynamicFieldsByForm(String formName) {
		return fieldRepository.findAllByForm_FormName(formName).stream().sorted(Comparator.comparing(FormField::getId))
				.map(formFieldMapper::mapToFieldResponse).toList();
	}

	@Override
	@Transactional
	public FormFieldResponse updateDynamicFieldById(String formName, @NonNull Long id,
			FormFieldRequest updateFieldRequest) throws ResourceNotFoundException, ResourceFoundException {
		FormField existingFormField = getById(id);
		formFieldMapper.updateFormFieldFromRequest(updateFieldRequest, existingFormField);
		existingFormField.setId(id);
		existingFormField.setForm(this.getOrCreateForm(formName));
		conversionEquals(existingFormField);
		// Map and set drop-down values
		List<DropDown> dropDownValues = mapDropDownValues(updateFieldRequest.getDropDowns(), existingFormField);
		existingFormField.setDropDowns(dropDownValues);

		// Update field by checking
		if (checkNotIdFieldInForm(existingFormField.getFieldName(), formName, id)) {
			throw new ResourceFoundException("Field with name '" + existingFormField.getFieldName()
					+ "' already exists in form '" + formName + "'.");
		}
		FormField updatedFormField = fieldRepository.save(existingFormField);
		return formFieldMapper.mapToFieldResponse(updatedFormField);
	}

	private List<DropDown> mapDropDownValues(List<DropDownRequest> dropDownRequests, FormField formField) {
		if (dropDownRequests != null) {
			return dropDownRequests.stream().map(dropDownRequest -> {
				DropDown dropDownValue = formFieldMapper.mapToDropDown(dropDownRequest);
				dropDownValue.setFormField(formField);
				return dropDownValue;
			}).toList();
		}
		return Collections.emptyList();
	}

	private Form getOrCreateForm(String formName) {
		Optional<Form> existingForm = formRepository.findByFormName(formName);

		return existingForm.orElseGet(() -> {
			Form newForm = new Form();
			newForm.setFormName(formName);
			return formRepository.save(newForm);
		});
	}

	@Override
	public void removeDynamicFieldById(@NonNull Long id) throws ResourceNotFoundException {
		FormField formField = this.getById(id);
		if (formField != null) {
			fieldRepository.delete(formField);
		}
	}

	private void conversionEquals(FormField formField) {
		String dataType = setFieldConversion(formField.getDataType());
		formField.setDataType(dataType);
		if (TEXT_FIELD_TYPE.equalsIgnoreCase(dataType) || TEXT_AREA_TYPE.equalsIgnoreCase(dataType)
				|| FILE_TYPE.equalsIgnoreCase(dataType)) {
			formField.setDropDowns(null);
			formField.setEnums(null);
		} else if (DROPDOWN_TYPE.equalsIgnoreCase(dataType)) {
			formField.setEnums(null);
		} else if (CHECKBOX_TYPE.equalsIgnoreCase(dataType)) {
			formField.setDropDowns(null);
		} else {
			if (formField.getEnums() == null) {
				formField.setEnums(new ArrayList<>());
			}
			if (formField.getDropDowns() == null) {
				formField.setDropDowns(new ArrayList<>());
			}
		}
	}

	private FormField getById(@NonNull Long id) throws ResourceNotFoundException {
		return fieldRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Field is not present"));

	}

	@Override
	public boolean checkFieldInForm(String fieldName, String formName) {
		return fieldRepository.existsByFieldNameAndForm_FormName(fieldName, formName);
	}

	private boolean checkNotIdFieldInForm(String fieldName, String formName, Long id) {
		return fieldRepository.existsByFieldNameAndForm_FormNameAndIdNot(fieldName, formName, id);
	}

	@Override
	public List<FormFieldResponse> getDynamicFieldsAndExistingFields(String formName, Boolean extraFields)
			throws ClassNotFoundException {
		List<FormFieldResponse> classFields = new ArrayList<>();

		// Extracted the retrieval of existing fields to a separate method
		if (extraFields == null) {
			classFields.addAll(this.getExistingFields(formName));
			classFields.addAll(this.getAllDynamicFieldsByForm(formName));
		} else if (!extraFields) {
			classFields.addAll(this.getExistingFields(formName));
		} else {
			classFields.addAll(this.getAllDynamicFieldsByForm(formName));
		}
		return classFields;
	}

	private Collection<? extends FormFieldResponse> getExistingFields(String formName) throws ClassNotFoundException {
		List<FormFieldResponse> existingFields = new ArrayList<>();
		List<String> moduleNames = getFormNames(formName);

		for (String modulName : moduleNames) {
			switch (modulName) {
			case "Plant":
				existingFields.addAll(plantServiceClient.getPlantExistingFields(formName));
				break;
			case "MrpType":
				existingFields.addAll(mrpServiceClient.getMrpExistingFields(formName));
				break;
			case "General":
				existingFields.addAll(generalServiceClient.getGeneralServiceExistingFields(formName));
				break;
			case "SalesOthers":
				existingFields.addAll(salesServiceClient.getSalesExistingFields(formName));
				break;
			case "Vendor":
				existingFields.addAll(vendorServiceClient.getVendorExistingFields(formName));
				break;
			case "Settings":
				existingFields.addAll(settingServiceClient.getGeneralSettingsExistingFields(formName));
				break;
			case "Attribute":
				existingFields.addAll(attributeServiceClient.getAttributeExistingFields(formName));
				break;
			case "Value":
				existingFields.addAll(valueServiceClient.getValueExistingFields(formName));
				break;
			default:
				break;
			}
		}

		return existingFields;
	}

	private List<String> getFormNames(String moduleName) {
		List<String> plant = Arrays.asList("Plant", "ProfitCenter", "PriceControl", "StorageLocation", "StorageBin",
				"VarianceKey", "ValuationClass", "Department");
		List<String> mrpType = Arrays.asList("MrpType", "MrpControl", "LotSize", "ProcurementType", "PlanningStrgyGrp",
				"AvailCheck", "ScheduleMargin");
		List<String> general = Arrays.asList("IndustrySector", "MaterialType", "BaseUOP", "UnitOfIssue", "AlternateUOM",
				"InspectionType", "InspectionCode", "Division", "SalesUnit");
		List<String> salesOthers = Arrays.asList("AccAssignment", "DeliveringPlant", "DistributionChannel",
				"ItemCategoryGroup", "LoadingGroup", "MaterialStrategicGroup", "OrderUnit", "PurchasingGroup",
				"PurchasingValueKey", "SalesOrganization", "TaxClassificaionClass", "TaxClassificationType",
				"TransportationGroup");
		List<String> vendor = Collections.singletonList("Vendor");
		List<String> settings = Arrays.asList("MainGroupCodes", "SubGroupCodes", "SubSubGroupCodes", "SourceType",
				"NMUOM", "ReferenceType", "AttributeUOM", "HSN");
		List<String> attribute = Collections.singletonList("Attribute");
		List<String> value = Collections.singletonList("Value");

		if (plant.contains(moduleName)) {
			return plant;
		} else if (mrpType.contains(moduleName)) {
			return mrpType;
		} else if (general.contains(moduleName)) {
			return general;
		} else if (salesOthers.contains(moduleName)) {
			return salesOthers;
		} else if (vendor.contains(moduleName)) {
			return vendor;
		} else if (settings.contains(moduleName)) {
			return settings;
		} else if (attribute.contains(moduleName)) {
			return attribute;
		} else if (value.contains(moduleName)) {
			return value;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<String> getAllFieldNamesOfForm(String formName) throws ClassNotFoundException {
		return getDynamicFieldsAndExistingFields(formName, null).stream().map(FormFieldResponse::getFieldName).toList();
	}

	@Override
	public List<Object> getListOfFieldNameValues(String displayName, String formName) {

		List<Object> fieldValues = new ArrayList<>();
		List<String> moduleNames = getFormNames(formName);

		for (String modulName : moduleNames) {
			switch (modulName) {
			case "Plant":
				fieldValues.addAll(plantServiceClient.getPlantListOfFieldNameValues(displayName, formName));
				break;
			case "MrpType":
				fieldValues.addAll(mrpServiceClient.getMrpListOfFieldNameValues(displayName, formName));
				break;
			case "General":
				fieldValues.addAll(generalServiceClient.getGeneralServiceListOfFieldNameValues(displayName, formName));
				break;
			case "SalesOthers":
				fieldValues.addAll(salesServiceClient.getSalesListOfFieldNameValues(displayName, formName));
				break;
			case "Vendor":
				fieldValues.addAll(vendorServiceClient.getVendorListOfFieldNameValues(displayName, formName));
				break;
			case "Settings":
				fieldValues.addAll(settingServiceClient.getSalesListOfFieldNameValues(displayName, formName));
				break;
			case "Attribute":
				fieldValues.addAll(attributeServiceClient.getAttributeListOfFieldNameValues(displayName, formName));
				break;
			case "Value":
				fieldValues.addAll(valueServiceClient.getValueListOfFieldNameValues(displayName, formName));
				break;
			default:
				break;
			}
		}

		return fieldValues;
	}

}
