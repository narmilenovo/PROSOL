package com.example.vendor_masterservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.vendor_masterservice.client.DynamicClient;
import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.entity.AuditFields;
import com.example.vendor_masterservice.entity.VendorMaster;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;
import com.example.vendor_masterservice.repository.VendorMasterRepository;
import com.example.vendor_masterservice.service.interfaces.VendorMasterService;
import com.example.vendor_masterservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorMasterServiceImpl implements VendorMasterService {
	private final VendorMasterRepository vendorMasterRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest) throws ResourceNotFoundException {
//		List<String> fieldsToSkipCapitalization = Arrays.asList("email", "website");
//		Helpers.inputTitleCase(vendorMasterRequest, fieldsToSkipCapitalization);
		VendorMaster vendorMaster = modelMapper.map(vendorMasterRequest, VendorMaster.class);
		for (Map.Entry<String, Object> entryField : vendorMaster.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = VendorMaster.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
		VendorMaster savedVendorMaster = vendorMasterRepository.save(vendorMaster);
		return mapToVendorMasterResponse(savedVendorMaster);
	}

	@Override
	public List<VendorMasterResponse> saveAllVm(List<VendorMasterRequest> vendorMasterRequests) {
		List<VendorMaster> vendorList = new ArrayList<>();
		for (VendorMasterRequest vendorMasterRequest : vendorMasterRequests) {
			List<String> fieldsToSkipCapitalization = Arrays.asList("email", "website");
			Helpers.inputTitleCase(vendorMasterRequest, fieldsToSkipCapitalization);
			VendorMaster vendorMaster = modelMapper.map(vendorMasterRequest, VendorMaster.class);
			vendorList.add(vendorMaster);
		}
		List<VendorMaster> savedList = vendorMasterRepository.saveAll(vendorList);
		return savedList.stream().map(this::mapToVendorMasterResponse).toList();
	}

	@Override
	public VendorMasterResponse getVmById(Long id) throws ResourceNotFoundException {
		VendorMaster vendorMaster = this.findVmById(id);
		return mapToVendorMasterResponse(vendorMaster);
	}

	@Override
	public List<VendorMasterResponse> getAllVm() {
		List<VendorMaster> vendorMasters = vendorMasterRepository.findAll();
		return vendorMasters.stream().sorted(Comparator.comparing(VendorMaster::getId))
				.map(this::mapToVendorMasterResponse).toList();
	}

	@Override
	public List<VendorMasterResponse> findAllStatusTrue() {
		List<VendorMaster> vendorMasters = vendorMasterRepository.findAllByStatusIsTrue();
		return vendorMasters.stream().sorted(Comparator.comparing(VendorMaster::getId))
				.map(this::mapToVendorMasterResponse).toList();
	}

	@Override
	public VendorMasterResponse updateVm(Long id, VendorMasterRequest updateVendorMasterRequest)
			throws ResourceNotFoundException {
//		List<String> fieldsToSkipCapitalization = Arrays.asList("email", "website");
//		Helpers.inputTitleCase(updateVendorMasterRequest, fieldsToSkipCapitalization);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		VendorMaster existingVendor = this.findVmById(id);
		if (!Objects.equals(existingVendor.getShortDescName(), updateVendorMasterRequest.getShortDescName())) {
			auditFields.add(new AuditFields(null, "ShortDesc Name", existingVendor.getShortDescName(),
					updateVendorMasterRequest.getShortDescName()));
			existingVendor.setShortDescName(updateVendorMasterRequest.getShortDescName());
		}
		if (!Objects.equals(existingVendor.getName(), updateVendorMasterRequest.getName())) {
			auditFields
					.add(new AuditFields(null, "Name", existingVendor.getName(), updateVendorMasterRequest.getName()));
			existingVendor.setName(updateVendorMasterRequest.getName());
		}
		if (!Objects.equals(existingVendor.getName2(), updateVendorMasterRequest.getName2())) {
			auditFields.add(
					new AuditFields(null, "Name2", existingVendor.getName2(), updateVendorMasterRequest.getName2()));
			existingVendor.setName2(updateVendorMasterRequest.getName2());
		}
		if (!Objects.equals(existingVendor.getName3(), updateVendorMasterRequest.getName3())) {
			auditFields.add(
					new AuditFields(null, "Name3", existingVendor.getName3(), updateVendorMasterRequest.getName3()));
			existingVendor.setName3(updateVendorMasterRequest.getName3());
		}
		if (!Objects.equals(existingVendor.getName4(), updateVendorMasterRequest.getName4())) {
			auditFields.add(
					new AuditFields(null, "Name4", existingVendor.getName4(), updateVendorMasterRequest.getName4()));
			existingVendor.setName4(updateVendorMasterRequest.getName4());
		}
		if (!Objects.equals(existingVendor.getAddress(), updateVendorMasterRequest.getAddress())) {
			auditFields.add(new AuditFields(null, "Address", existingVendor.getAddress(),
					updateVendorMasterRequest.getAddress()));
			existingVendor.setAddress(updateVendorMasterRequest.getAddress());
		}
		if (!Objects.equals(existingVendor.getAddress2(), updateVendorMasterRequest.getAddress2())) {
			auditFields.add(new AuditFields(null, "Address2", existingVendor.getAddress2(),
					updateVendorMasterRequest.getAddress2()));
			existingVendor.setAddress2(updateVendorMasterRequest.getAddress2());
		}
		if (!Objects.equals(existingVendor.getAddress3(), updateVendorMasterRequest.getAddress3())) {
			auditFields.add(new AuditFields(null, "Address3", existingVendor.getAddress3(),
					updateVendorMasterRequest.getAddress3()));
			existingVendor.setAddress3(updateVendorMasterRequest.getAddress3());
		}
		if (!Objects.equals(existingVendor.getAddress4(), updateVendorMasterRequest.getAddress4())) {
			auditFields.add(new AuditFields(null, "Address4", existingVendor.getAddress4(),
					updateVendorMasterRequest.getAddress4()));
			existingVendor.setAddress4(updateVendorMasterRequest.getAddress4());
		}
		if (!Objects.equals(existingVendor.getCity(), updateVendorMasterRequest.getCity())) {
			auditFields
					.add(new AuditFields(null, "City", existingVendor.getCity(), updateVendorMasterRequest.getCity()));
			existingVendor.setCity(updateVendorMasterRequest.getCity());
		}
		if (!Objects.equals(existingVendor.getState(), updateVendorMasterRequest.getState())) {
			auditFields.add(
					new AuditFields(null, "State", existingVendor.getState(), updateVendorMasterRequest.getState()));
			existingVendor.setState(updateVendorMasterRequest.getState());
		}
		if (!Objects.equals(existingVendor.getCountry(), updateVendorMasterRequest.getCountry())) {
			auditFields.add(new AuditFields(null, "Country", existingVendor.getCountry(),
					updateVendorMasterRequest.getCountry()));
			existingVendor.setCountry(updateVendorMasterRequest.getCountry());
		}
		if (!Objects.equals(existingVendor.getPostalCode(), updateVendorMasterRequest.getPostalCode())) {
			auditFields.add(new AuditFields(null, "PostalCode", existingVendor.getPostalCode(),
					updateVendorMasterRequest.getPostalCode()));
			existingVendor.setPostalCode(updateVendorMasterRequest.getPostalCode());
		}
		if (!Objects.equals(existingVendor.getTelephoneNo(), updateVendorMasterRequest.getTelephoneNo())) {
			auditFields.add(new AuditFields(null, "TelephoneNo", existingVendor.getTelephoneNo(),
					updateVendorMasterRequest.getTelephoneNo()));
			existingVendor.setTelephoneNo(updateVendorMasterRequest.getTelephoneNo());
		}
		if (!Objects.equals(existingVendor.getFax(), updateVendorMasterRequest.getFax())) {
			auditFields.add(new AuditFields(null, "Fax", existingVendor.getFax(), updateVendorMasterRequest.getFax()));
			existingVendor.setFax(updateVendorMasterRequest.getFax());
		}
		if (!Objects.equals(existingVendor.getMobileNo(), updateVendorMasterRequest.getMobileNo())) {
			auditFields.add(new AuditFields(null, "MobileNo", existingVendor.getMobileNo(),
					updateVendorMasterRequest.getMobileNo()));
			existingVendor.setMobileNo(updateVendorMasterRequest.getMobileNo());

		}
		if (!Objects.equals(existingVendor.getEmail(), updateVendorMasterRequest.getEmail())) {
			auditFields.add(
					new AuditFields(null, "Email", existingVendor.getEmail(), updateVendorMasterRequest.getEmail()));
			existingVendor.setEmail(updateVendorMasterRequest.getEmail());
		}
		if (!Objects.equals(existingVendor.getWebsite(), updateVendorMasterRequest.getWebsite())) {
			auditFields.add(new AuditFields(null, "Website", existingVendor.getWebsite(),
					updateVendorMasterRequest.getWebsite()));
			existingVendor.setWebsite(updateVendorMasterRequest.getWebsite());
		}

		if (!Objects.equals(existingVendor.getAcquiredBy(), updateVendorMasterRequest.getAcquiredBy())) {
			auditFields.add(new AuditFields(null, "AcquiredBy", existingVendor.getAcquiredBy(),
					updateVendorMasterRequest.getAcquiredBy()));
			existingVendor.setAcquiredBy(updateVendorMasterRequest.getAcquiredBy());

		}
		if (!Objects.equals(existingVendor.getStatus(), updateVendorMasterRequest.getStatus())) {
			auditFields.add(
					new AuditFields(null, "Status", existingVendor.getStatus(), updateVendorMasterRequest.getStatus()));
			existingVendor.setStatus(updateVendorMasterRequest.getStatus());
		}
		if (!Objects.equals(existingVendor.getDynamicFields(), updateVendorMasterRequest.getDynamicFields())) {
			for (Map.Entry<String, Object> entry : updateVendorMasterRequest.getDynamicFields().entrySet()) {
				String fieldName = entry.getKey();
				Object newValue = entry.getValue();
				Object oldValue = existingVendor.getDynamicFields().get(fieldName);
				if (oldValue == null || !oldValue.equals(newValue)) {
					auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
					existingVendor.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
				}
			}
		}
		existingVendor.updateAuditHistory(auditFields);
		VendorMaster updatedVendor = vendorMasterRepository.save(existingVendor);
		return mapToVendorMasterResponse(updatedVendor);
	}

	@Override
	public VendorMasterResponse updateVmStatusById(Long id) throws ResourceNotFoundException {
		VendorMaster existingVendorMaster = this.findVmById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingVendorMaster.getStatus() != null) {
			auditFields.add(new AuditFields(null, "Status", existingVendorMaster.getStatus(),
					!existingVendorMaster.getStatus()));
			existingVendorMaster.setStatus(!existingVendorMaster.getStatus());
		}
		existingVendorMaster.updateAuditHistory(auditFields);
		vendorMasterRepository.save(existingVendorMaster);
		return this.mapToVendorMasterResponse(existingVendorMaster);
	}

	@Override
	public List<VendorMasterResponse> updateBulkStatusVmId(List<Long> id) throws ResourceNotFoundException {
		List<VendorMaster> vendorMasters = this.findAllVendorById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		vendorMasters.stream().forEach(existingVendorMaster -> {
			if (existingVendorMaster.getStatus() != null) {
				auditFields.add(new AuditFields(null, "Status", existingVendorMaster.getStatus(),
						!existingVendorMaster.getStatus()));
				existingVendorMaster.setStatus(!existingVendorMaster.getStatus());
			}
			existingVendorMaster.updateAuditHistory(auditFields);
		});
		vendorMasterRepository.saveAll(vendorMasters);
		return vendorMasters.stream().map(this::mapToVendorMasterResponse).toList();
	}

	@Override
	public void deleteVmId(Long id) throws ResourceNotFoundException {
		VendorMaster vendorMaster = this.findVmById(id);
		vendorMasterRepository.deleteById(vendorMaster.getId());
	}

	private VendorMaster findVmById(Long id) throws ResourceNotFoundException {
		Optional<VendorMaster> vendorMaster = vendorMasterRepository.findById(id);
		if (vendorMaster.isEmpty()) {
			throw new ResourceNotFoundException("Vendor Master with this ID Not found");
		}
		return vendorMaster.get();
	}

	private VendorMasterResponse mapToVendorMasterResponse(VendorMaster vendorMaster) {
		return modelMapper.map(vendorMaster, VendorMasterResponse.class);
	}

	@Override
	public void deleteVmBatchById(List<Long> id) throws ResourceNotFoundException {
		List<VendorMaster> vendorMasters = this.findAllVendorById(id);
		vendorMasterRepository.deleteAll(vendorMasters);
	}

	private List<VendorMaster> findAllVendorById(List<Long> ids) throws ResourceNotFoundException {
		List<VendorMaster> vendorMasters = vendorMasterRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> vendorMasters.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Vendor Master with IDs " + missingIds + " not found.");
		}
		return vendorMasters;
	}

}
