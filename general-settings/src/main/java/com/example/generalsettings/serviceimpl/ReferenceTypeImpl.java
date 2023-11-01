package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.ReferenceType;
import com.example.generalsettings.repo.ReferenceTypeRepo;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;
import com.example.generalsettings.service.ReferenceTypeService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ReferenceTypeImpl implements ReferenceTypeService {
	 private final ReferenceTypeRepo referenceTypeRepo;

	    private final ModelMapper modelMapper;

	    public static final String REFERENCE_TYPE_NOT_FOUND_MESSAGE = null;


	    @Override
	    public List<ReferenceTypeResponse> getAllReferenceType() {
	        List<ReferenceType> referenceType = referenceTypeRepo.findAll();
	        return referenceType.stream().map(this::mapToReferenceTypeResponse).toList();
	    }

	    @Override
	    public ReferenceTypeResponse updateReferenceType(Long id, ReferenceTypeRequest referenceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {

	        Optional<ReferenceType> existReferenceTypeName = referenceTypeRepo.findByReferenceTypeName(referenceTypeRequest.getReferenceTypeName());
	        if (existReferenceTypeName.isPresent() && !existReferenceTypeName.get().getReferenceTypeName().equals(referenceTypeRequest.getReferenceTypeName())) {
	            throw new AlreadyExistsException("ReferenceType with this name already exists");
	        } else {
	            ReferenceType existingReferenceType = this.findReferenceTypeById(id);
	            modelMapper.map(referenceTypeRequest, existingReferenceType);
	            referenceTypeRepo.save(existingReferenceType);
	            return mapToReferenceTypeResponse(existingReferenceType);
	        }
	    }

	    public void deleteReferenceType(Long id) throws ResourceNotFoundException {
	        ReferenceType referenceType = this.findReferenceTypeById(id);
	        referenceTypeRepo.deleteById(referenceType.getId());
	    }

	    @Override
	    public ReferenceTypeResponse saveReferenceType(ReferenceTypeRequest referenceTypeRequest) throws  AlreadyExistsException {

	        Optional<ReferenceType> existReferenceTypeName = referenceTypeRepo.findByReferenceTypeName(referenceTypeRequest.getReferenceTypeName());
	        if (existReferenceTypeName.isPresent() ) {
	            throw new AlreadyExistsException("ReferenceType with this name already exists");
	        } else {
	            ReferenceType referenceType = modelMapper.map(referenceTypeRequest, ReferenceType.class);
	            referenceTypeRepo.save(referenceType);
	            return mapToReferenceTypeResponse(referenceType);
	        }
	    }


	    private ReferenceTypeResponse mapToReferenceTypeResponse(ReferenceType referenceType) {
	        return modelMapper.map(referenceType, ReferenceTypeResponse.class);
	    }

	    private ReferenceType findReferenceTypeById(Long id) throws ResourceNotFoundException {
	        Optional<ReferenceType> referenceType = referenceTypeRepo.findById(id);
	        if (referenceType.isEmpty()) {
	            throw new ResourceNotFoundException(REFERENCE_TYPE_NOT_FOUND_MESSAGE);
	        }
	        return referenceType.get();
	    }



	    @Override
	    public ReferenceTypeResponse getReferenceTypeById(Long id) throws ResourceNotFoundException {
	        ReferenceType referenceType = this.findReferenceTypeById(id);
	        return mapToReferenceTypeResponse(referenceType);
	    }

	    @Override
	    public List<ReferenceTypeResponse> updateBulkStatusReferenceTypeId(List<Long> id) {
	        List<ReferenceType> existingReferenceType = referenceTypeRepo.findAllById(id);
	        for (ReferenceType referenceType : existingReferenceType) {
	            referenceType.setReferenceTypeStatus(!referenceType.getReferenceTypeStatus());
	        }
	        referenceTypeRepo.saveAll(existingReferenceType);
	        return existingReferenceType.stream().map(this::mapToReferenceTypeResponse).toList();
	    }

	    @Override
	    public ReferenceTypeResponse updateStatusUsingReferenceTypeId(Long id) throws ResourceNotFoundException {
	        ReferenceType existingReferenceType = this.findReferenceTypeById(id);
	        existingReferenceType.setReferenceTypeStatus(!existingReferenceType.getReferenceTypeStatus());
	        referenceTypeRepo.save(existingReferenceType);
	        return mapToReferenceTypeResponse(existingReferenceType);
	    }
}
