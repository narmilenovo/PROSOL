package com.example.vendor_masterservice.service;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.entity.VendorMaster;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;
import com.example.vendor_masterservice.repository.VendorMasterRepository;
import com.example.vendor_masterservice.service.interfaces.VendorMasterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorMasterServiceImpl implements VendorMasterService {
    private final VendorMasterRepository vendorMasterRepository;
    private final ModelMapper modelMapper;

    @Override
    public VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest) {
        VendorMaster vendorMaster = modelMapper.map(vendorMasterRequest, VendorMaster.class);
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
    @Cacheable("vendor")
    public List<VendorMasterResponse> getAllVm() {
        List<VendorMaster> vendorMasters = vendorMasterRepository.findAll();
        return vendorMasters.stream()
                .sorted(Comparator.comparing(VendorMaster::getId))
                .map(this::mapToVendorMasterResponse)
                .toList();
    }

    @Override
    @Cacheable("vendor")
    public VendorMasterResponse getVmById(Long id) throws ResourceNotFoundException {
        VendorMaster vendorMaster = this.findVmById(id);
        return mapToVendorMasterResponse(vendorMaster);
    }

    @Override
    @Cacheable("vendor")
    public List<VendorMasterResponse> findAllStatusTrue() {
        List<VendorMaster> vendorMasters = vendorMasterRepository.findAllByStatusIsTrue();
        return vendorMasters.stream()
                .sorted(Comparator.comparing(VendorMaster::getId))
                .map(this::mapToVendorMasterResponse)
                .toList();
    }

    @Override
    public VendorMasterResponse updateVm(Long id, VendorMasterRequest updateVendorMasterRequest) throws ResourceNotFoundException {
        VendorMaster existingVendor = this.findVmById(id);
        modelMapper.map(existingVendor, updateVendorMasterRequest);
        VendorMaster updatedVendor = vendorMasterRepository.save(existingVendor);
        return mapToVendorMasterResponse(updatedVendor);
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

}
