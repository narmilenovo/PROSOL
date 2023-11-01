package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.SourceType;
import com.example.generalsettings.repo.SourceTypeRepo;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;
import com.example.generalsettings.service.SourceTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SourceTypeServiceImpl implements SourceTypeService{
	  private final SourceTypeRepo sourceTypeRepo;

	    private final ModelMapper modelMapper;

	    public static final String SOURCE_TYPE_NOT_FOUND_MESSAGE = null;


	    @Override
	    public List<SourceTypeResponse> getAllSourceType() {
	        List<SourceType> sourceType = sourceTypeRepo.findAll();
	        return sourceType.stream().map(this::mapToSourceTypeResponse).toList();
	    }

	    @Override
	    public SourceTypeResponse updateSourceType(Long id, SourceTypeRequest sourceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {

	        Optional<SourceType> existSourceTypeName = sourceTypeRepo.findBySourceTypeName(sourceTypeRequest.getSourceTypeName());
	        if (existSourceTypeName.isPresent() && !existSourceTypeName.get().getSourceTypeName().equals(sourceTypeRequest.getSourceTypeName())) {
	            throw new AlreadyExistsException("SourceType with this name already exists");
	        } else {
	            SourceType existingSourceType = this.findSourceTypeById(id);
	            modelMapper.map(sourceTypeRequest, existingSourceType);
	            sourceTypeRepo.save(existingSourceType);
	            return mapToSourceTypeResponse(existingSourceType);
	        }
	    }

	    public void deleteSourceType(Long id) throws ResourceNotFoundException {
	        SourceType sourceType = this.findSourceTypeById(id);
	        sourceTypeRepo.deleteById(sourceType.getId());
	    }

	    @Override
	    public SourceTypeResponse saveSourceType(SourceTypeRequest sourceTypeRequest) throws  AlreadyExistsException {

	        Optional<SourceType> existSourceTypeName = sourceTypeRepo.findBySourceTypeName(sourceTypeRequest.getSourceTypeName());
	        if (existSourceTypeName.isPresent() ) {
	            throw new AlreadyExistsException("SourceType with this name already exists");
	        } else {
	            SourceType sourceType = modelMapper.map(sourceTypeRequest, SourceType.class);
	            sourceTypeRepo.save(sourceType);
	            return mapToSourceTypeResponse(sourceType);
	        }
	    }


	    private SourceTypeResponse mapToSourceTypeResponse(SourceType sourceType) {
	        return modelMapper.map(sourceType, SourceTypeResponse.class);
	    }

	    private SourceType findSourceTypeById(Long id) throws ResourceNotFoundException {
	        Optional<SourceType> sourceType = sourceTypeRepo.findById(id);
	        if (sourceType.isEmpty()) {
	            throw new ResourceNotFoundException(SOURCE_TYPE_NOT_FOUND_MESSAGE);
	        }
	        return sourceType.get();
	    }



	    @Override
	    public SourceTypeResponse getSourceTypeById(Long id) throws ResourceNotFoundException {
	        SourceType sourceType = this.findSourceTypeById(id);
	        return mapToSourceTypeResponse(sourceType);
	    }

	    @Override
	    public List<SourceTypeResponse> updateBulkStatusSourceTypeId(List<Long> id) {
	        List<SourceType> existingSourceType = sourceTypeRepo.findAllById(id);
	        for (SourceType sourceType : existingSourceType) {
	            sourceType.setSourceTypeStatus(!sourceType.getSourceTypeStatus());
	        }
	        sourceTypeRepo.saveAll(existingSourceType);
	        return existingSourceType.stream().map(this::mapToSourceTypeResponse).toList();
	    }

	    @Override
	    public SourceTypeResponse updateStatusUsingSourceTypeId(Long id) throws ResourceNotFoundException {
	        SourceType existingSourceType = this.findSourceTypeById(id);
	        existingSourceType.setSourceTypeStatus(!existingSourceType.getSourceTypeStatus());
	        sourceTypeRepo.save(existingSourceType);
	        return mapToSourceTypeResponse(existingSourceType);
	    }
}
