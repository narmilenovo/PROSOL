package com.example.vendor_masterservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.vendor_masterservice.client.DynamicClient;
import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.entity.AuditFields;
import com.example.vendor_masterservice.entity.VendorMaster;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;
import com.example.vendor_masterservice.mapping.VendorMapper;
import com.example.vendor_masterservice.repository.VendorMasterRepository;
import com.example.vendor_masterservice.service.interfaces.VendorMasterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorMasterServiceImpl implements VendorMasterService {
	private final VendorMasterRepository vendorMasterRepository;
	private final VendorMapper vendorMapper;
	private final DynamicClient dynamicClient;

	@Override
	public VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest) throws ResourceNotFoundException {
		VendorMaster vendorMaster = vendorMapper.mapToVendorMaster(vendorMasterRequest);

		validateDynamicFields(vendorMaster);

		VendorMaster savedVendorMaster = vendorMasterRepository.save(vendorMaster);
		return vendorMapper.mapToVendorMasterResponse(savedVendorMaster);
	}

	@Override
	public List<VendorMasterResponse> saveAllVm(List<VendorMasterRequest> vendorMasterRequests) {
		List<VendorMaster> vendorList = new ArrayList<>();
		for (VendorMasterRequest vendorMasterRequest : vendorMasterRequests) {
			VendorMaster vendorMaster = vendorMapper.mapToVendorMaster(vendorMasterRequest);
			vendorList.add(vendorMaster);
		}
		List<VendorMaster> savedList = vendorMasterRepository.saveAll(vendorList);
		return savedList.stream().map(vendorMapper::mapToVendorMasterResponse).toList();
	}

	@Override
	public VendorMasterResponse getVmById(@NonNull Long id) throws ResourceNotFoundException {
		VendorMaster vendorMaster = this.findVmById(id);
		return vendorMapper.mapToVendorMasterResponse(vendorMaster);
	}

	@Override
	public List<VendorMasterResponse> getAllVm() {
		List<VendorMaster> vendorMasters = vendorMasterRepository.findAll();
		return vendorMasters.stream().sorted(Comparator.comparing(VendorMaster::getId))
				.map(vendorMapper::mapToVendorMasterResponse).toList();
	}

	@Override
	public List<VendorMasterResponse> findAllStatusTrue() {
		List<VendorMaster> vendorMasters = vendorMasterRepository.findAllByStatusIsTrue();
		return vendorMasters.stream().sorted(Comparator.comparing(VendorMaster::getId))
				.map(vendorMapper::mapToVendorMasterResponse).toList();
	}

	@Override
	public VendorMasterResponse updateVm(@NonNull Long id, VendorMasterRequest updateVendorMasterRequest)
			throws ResourceNotFoundException {
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
		return vendorMapper.mapToVendorMasterResponse(updatedVendor);
	}

	@Override
	public VendorMasterResponse updateVmStatusById(@NonNull Long id) throws ResourceNotFoundException {
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
		return vendorMapper.mapToVendorMasterResponse(existingVendorMaster);
	}

	@Override
	public List<VendorMasterResponse> updateBulkStatusVmId(@NonNull List<Long> id) throws ResourceNotFoundException {
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
		return vendorMasters.stream().map(vendorMapper::mapToVendorMasterResponse).toList();
	}

	@Override
	public void deleteVmId(@NonNull Long id) throws ResourceNotFoundException {
		VendorMaster vendorMaster = this.findVmById(id);
		if (vendorMaster != null) {
			vendorMasterRepository.delete(vendorMaster);
		}
	}

	private void validateDynamicFields(VendorMaster vendorMaster) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : vendorMaster.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = VendorMaster.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private VendorMaster findVmById(@NonNull Long id) throws ResourceNotFoundException {
		return vendorMasterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Vendor Master with this ID Not found"));
	}

	@Override
	public void deleteVmBatchById(@NonNull List<Long> id) throws ResourceNotFoundException {
		List<VendorMaster> vendorMasters = this.findAllVendorById(id);
		if (!vendorMasters.isEmpty()) {
			vendorMasterRepository.deleteAll(vendorMasters);
		}
	}

	private List<VendorMaster> findAllVendorById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<VendorMaster> vendorMasters = vendorMasterRepository.findAllById(ids);

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Vendor Master with IDs " + missingIds + " not found.");
		}

		return vendorMasters;
	}

}
