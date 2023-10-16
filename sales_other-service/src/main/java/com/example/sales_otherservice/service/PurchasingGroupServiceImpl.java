package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.entity.PurchasingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.PurchasingGroupRepository;
import com.example.sales_otherservice.service.interfaces.PurchasingGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchasingGroupServiceImpl implements PurchasingGroupService {
    private final PurchasingGroupRepository purchasingGroupRepository;
    private final ModelMapper modelMapper;

    @Override
    public PurchasingGroupResponse savePg(PurchasingGroupRequest purchasingGroupRequest) throws ResourceFoundException {
        String pgCode = purchasingGroupRequest.getPgCode();
        String pgName = purchasingGroupRequest.getPgName();
        boolean exists = purchasingGroupRepository.existsByPgCodeOrPgName(pgCode, pgName);
        if (!exists) {

            PurchasingGroup purchasingGroup = modelMapper.map(purchasingGroupRequest, PurchasingGroup.class);
            PurchasingGroup savedPurchasingGroup = purchasingGroupRepository.save(purchasingGroup);
            return mapToPurchasingGroupResponse(savedPurchasingGroup);
        }
        throw new ResourceFoundException("Purchasing Group Already exist");
    }

    @Override
    public List<PurchasingGroupResponse> getAllPg() {
        List<PurchasingGroup> purchasingGroups = purchasingGroupRepository.findAll();
        return purchasingGroups.stream().map(this::mapToPurchasingGroupResponse).toList();

    }

    @Override
    public PurchasingGroupResponse getPgById(Long id) throws ResourceNotFoundException {
        PurchasingGroup purchasingGroup = this.findPgById(id);
        return mapToPurchasingGroupResponse(purchasingGroup);
    }

    @Override
    public List<PurchasingGroupResponse> findAllStatusTrue() {
        List<PurchasingGroup> purchasingGroups = purchasingGroupRepository.findAllByPgStatusIsTrue();
        return purchasingGroups.stream().map(this::mapToPurchasingGroupResponse).toList();
    }

    @Override
    public PurchasingGroupResponse updatePg(Long id, PurchasingGroupRequest updatePurchasingGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        String pgCode = updatePurchasingGroupRequest.getPgCode();
        String pgName = updatePurchasingGroupRequest.getPgName();
        PurchasingGroup existingPurchasingGroup = this.findPgById(id);
        boolean exists = purchasingGroupRepository.existsByPgCodeAndIdNotOrPgNameAndIdNot(pgCode, id, pgName, id);
        if (!exists) {
            modelMapper.map(updatePurchasingGroupRequest, existingPurchasingGroup);
            PurchasingGroup updatedPurchasingGroup = purchasingGroupRepository.save(existingPurchasingGroup);
            return mapToPurchasingGroupResponse(updatedPurchasingGroup);
        }
        throw new ResourceFoundException("Purchasing Group Already exist");
    }

    @Override
    public void deletePgById(Long id) throws ResourceNotFoundException {
        PurchasingGroup purchasingGroup = this.findPgById(id);
        purchasingGroupRepository.deleteById(purchasingGroup.getId());
    }

    private PurchasingGroupResponse mapToPurchasingGroupResponse(PurchasingGroup purchasingGroup) {
        return modelMapper.map(purchasingGroup, PurchasingGroupResponse.class);
    }

    private PurchasingGroup findPgById(Long id) throws ResourceNotFoundException {
        Optional<PurchasingGroup> purchasingGroup = purchasingGroupRepository.findById(id);
        if (purchasingGroup.isEmpty()) {
            throw new ResourceNotFoundException("Purchasing Group not found with this Id");
        }
        return purchasingGroup.get();
    }
}
