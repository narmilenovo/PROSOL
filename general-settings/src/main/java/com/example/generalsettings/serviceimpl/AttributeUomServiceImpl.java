package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.AttributeUom;
import com.example.generalsettings.repo.AttributeUomRepo;
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;
import com.example.generalsettings.service.AttributeUomService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AttributeUomServiceImpl implements AttributeUomService {
	  private final ModelMapper modelMapper;
	 private final AttributeUomRepo attributeUomRepo;

	    public static final String ATTRIBUTE_TYPE_NOT_FOUND_MESSAGE = null;

	    @Override
	    public List<AttributeUomResponse> getAllAttributeUom() {
	        List<AttributeUom> attributeUom = attributeUomRepo.findAll();
	        return attributeUom.stream().map(this::mapToAttributeUomResponse).toList();
	    }

	    @Override
	    public AttributeUomResponse updateAttributeUom(Long id, AttributeUomRequest attributeUomRequest) throws ResourceNotFoundException, AlreadyExistsException {

	        Optional<AttributeUom> existAttributeUomName = attributeUomRepo.findByAttributeUomName(attributeUomRequest.getAttributeUomName());
	        if (existAttributeUomName.isPresent() && !existAttributeUomName.get().getAttributeUomName().equals(attributeUomRequest.getAttributeUomName())) {
	            throw new AlreadyExistsException("AttributeUom with this name already exists");
	        } else {
	            AttributeUom existingAttributeUom = this.findAttributeUomById(id);
	            modelMapper.map(attributeUomRequest, existingAttributeUom);
	            attributeUomRepo.save(existingAttributeUom);
	            return mapToAttributeUomResponse(existingAttributeUom);
	        }
	    }

	    public void deleteAttributeUom(Long id) throws ResourceNotFoundException {
	        AttributeUom attributeUom = this.findAttributeUomById(id);
	        attributeUomRepo.deleteById(attributeUom.getId());
	    }

	    @Override
	    public AttributeUomResponse saveAttributeUom(AttributeUomRequest attributeUomRequest) throws  AlreadyExistsException {

	        Optional<AttributeUom> existAttributeUomName = attributeUomRepo.findByAttributeUomName(attributeUomRequest.getAttributeUomName());
	        if (existAttributeUomName.isPresent() ) {
	            throw new AlreadyExistsException("AttributeUom with this name already exists");
	        } else {
	            AttributeUom attributeUom = modelMapper.map(attributeUomRequest, AttributeUom.class);
	            attributeUomRepo.save(attributeUom);
	            return mapToAttributeUomResponse(attributeUom);
	        }
	    }


	    private AttributeUomResponse mapToAttributeUomResponse(AttributeUom attributeUom) {
	        return modelMapper.map(attributeUom, AttributeUomResponse.class);
	    }

	    private AttributeUom findAttributeUomById(Long id) throws ResourceNotFoundException {
	        Optional<AttributeUom> attributeUom = attributeUomRepo.findById(id);
	        if (attributeUom.isEmpty()) {
	            throw new ResourceNotFoundException(ATTRIBUTE_TYPE_NOT_FOUND_MESSAGE);
	        }
	        return attributeUom.get();
	    }



	    @Override
	    public AttributeUomResponse getAttributeUomById(Long id) throws ResourceNotFoundException {
	        AttributeUom attributeUom = this.findAttributeUomById(id);
	        return mapToAttributeUomResponse(attributeUom);
	    }

	    @Override
	    public List<AttributeUomResponse> updateBulkStatusAttributeUomId(List<Long> id) {
	        List<AttributeUom> existingAttributeUom = attributeUomRepo.findAllById(id);
	        for (AttributeUom attributeUom : existingAttributeUom) {
	            attributeUom.setAttributeUomStatus(!attributeUom.getAttributeUomStatus());
	        }
	        attributeUomRepo.saveAll(existingAttributeUom);
	        return existingAttributeUom.stream().map(this::mapToAttributeUomResponse).toList();
	    }

	    @Override
	    public AttributeUomResponse updateStatusUsingAttributeUomId(Long id) throws ResourceNotFoundException {
	        AttributeUom existingAttributeUom = this.findAttributeUomById(id);
	        existingAttributeUom.setAttributeUomStatus(!existingAttributeUom.getAttributeUomStatus());
	        attributeUomRepo.save(existingAttributeUom);
	        return mapToAttributeUomResponse(existingAttributeUom);
	    }
}
