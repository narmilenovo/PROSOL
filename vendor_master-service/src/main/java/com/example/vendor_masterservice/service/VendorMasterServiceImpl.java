package com.example.vendor_masterservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.vendor_masterservice.client.DynamicClient;
import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.entity.VendorMaster;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;
import com.example.vendor_masterservice.repository.VendorMasterRepository;
import com.example.vendor_masterservice.service.interfaces.VendorMasterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorMasterServiceImpl implements VendorMasterService {
	private final VendorMasterRepository vendorMasterRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest) throws ResourceNotFoundException {
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
		VendorMaster existingVendor = this.findVmById(id);
//		modelMapper.map(existingVendor, updateVendorMasterRequest);
		for (Map.Entry<String, Object> entryField : updateVendorMasterRequest.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = VendorMaster.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
		existingVendor.setId(id);
		existingVendor.setShortDescName(updateVendorMasterRequest.getShortDescName());
		existingVendor.setName(updateVendorMasterRequest.getName());
		existingVendor.setName2(updateVendorMasterRequest.getName2());
		existingVendor.setName3(updateVendorMasterRequest.getName3());
		existingVendor.setName4(updateVendorMasterRequest.getName4());
		existingVendor.setAddress(updateVendorMasterRequest.getAddress());
		existingVendor.setAddress2(updateVendorMasterRequest.getAddress2());
		existingVendor.setAddress3(updateVendorMasterRequest.getAddress3());
		existingVendor.setAddress4(updateVendorMasterRequest.getAddress4());
		existingVendor.setCity(updateVendorMasterRequest.getCity());
		existingVendor.setState(updateVendorMasterRequest.getState());
		existingVendor.setCountry(updateVendorMasterRequest.getCountry());
		existingVendor.setPostalCode(updateVendorMasterRequest.getPostalCode());
		existingVendor.setTelephoneNo(updateVendorMasterRequest.getTelephoneNo());
		existingVendor.setFax(updateVendorMasterRequest.getFax());
		existingVendor.setMobileNo(updateVendorMasterRequest.getMobileNo());
		existingVendor.setEmail(updateVendorMasterRequest.getEmail());
		existingVendor.setWebsite(updateVendorMasterRequest.getWebsite());
		existingVendor.setAcquiredBy(updateVendorMasterRequest.getAcquiredBy());
		existingVendor.setStatus(updateVendorMasterRequest.getStatus());
		VendorMaster updatedVendor = vendorMasterRepository.save(existingVendor);
		return mapToVendorMasterResponse(updatedVendor);
	}

	@Override
	public VendorMasterResponse updateVmStatusById(Long id) throws ResourceNotFoundException {
		VendorMaster vendorMaster = this.findVmById(id);
		vendorMasterRepository.save(vendorMaster);
		return this.mapToVendorMasterResponse(vendorMaster);
	}

	@Override
	public List<VendorMasterResponse> updateBulkStatusVmId(List<Long> id) throws ResourceNotFoundException {
		List<VendorMaster> vendorMasters = this.findAllVendorById(id);
		vendorMasters.stream().forEach(vendorMaster -> vendorMaster.setStatus(!vendorMaster.getStatus()));
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
