package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.NmUom;
import com.example.generalsettings.repo.NmUomRepo;
import com.example.generalsettings.request.NmUomRequest;
import com.example.generalsettings.response.NmUomResponse;
import com.example.generalsettings.service.NmUomService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class NmUomServiceImpl implements NmUomService{
	   private final NmUomRepo nmUomRepo;

	    private final ModelMapper modelMapper;

	    public static final String NM_UOM_NOT_FOUND_MESSAGE = null;


	    @Override
	    public List<NmUomResponse> getAllNmUom() {
	        List<NmUom> nmUom = nmUomRepo.findAll();
	        return nmUom.stream().map(this::mapToNmUomResponse).toList();
	    }

	    @Override
	    public NmUomResponse updateNmUom(Long id, NmUomRequest nmUomRequest) throws ResourceNotFoundException, AlreadyExistsException {

	        Optional<NmUom> existNmUomName = nmUomRepo.findByName(nmUomRequest.getName());
	        if (existNmUomName.isPresent() && !existNmUomName.get().getName().equals(nmUomRequest.getName())) {
	            throw new AlreadyExistsException("NmUom with this name already exists");
	        } else {
	            NmUom existingNmUom = this.findNmUomById(id);
	            modelMapper.map(nmUomRequest, existingNmUom);
	            nmUomRepo.save(existingNmUom);
	            return mapToNmUomResponse(existingNmUom);
	        }
	    }

	    public void deleteNmUom(Long id) throws ResourceNotFoundException {
	        NmUom nmUom = this.findNmUomById(id);
	        nmUomRepo.deleteById(nmUom.getId());
	    }

	    @Override
	    public NmUomResponse saveNmUom(NmUomRequest nmUomRequest) throws  AlreadyExistsException {

	        Optional<NmUom> existNmUomName = nmUomRepo.findByName(nmUomRequest.getName());
	        if (existNmUomName.isPresent() ) {
	            throw new AlreadyExistsException("NmUom with this name already exists");
	        } else {
	            NmUom nmUom = modelMapper.map(nmUomRequest, NmUom.class);
	            nmUomRepo.save(nmUom);
	            return mapToNmUomResponse(nmUom);
	        }
	    }


	    private NmUomResponse mapToNmUomResponse(NmUom nmUom) {
	        return modelMapper.map(nmUom, NmUomResponse.class);
	    }

	    private NmUom findNmUomById(Long id) throws ResourceNotFoundException {
	        Optional<NmUom> nmUom = nmUomRepo.findById(id);
	        if (nmUom.isEmpty()) {
	            throw new ResourceNotFoundException(NM_UOM_NOT_FOUND_MESSAGE);
	        }
	        return nmUom.get();
	    }



	    @Override
	    public NmUomResponse getNmUomById(Long id) throws ResourceNotFoundException {
	        NmUom nmUom = this.findNmUomById(id);
	        return mapToNmUomResponse(nmUom);
	    }

	    @Override
	    public List<NmUomResponse> updateBulkStatusNmUomId(List<Long> id) {
	        List<NmUom> existingNmUom = nmUomRepo.findAllById(id);
	        for (NmUom nmUom : existingNmUom) {
	            nmUom.setStatus(!nmUom.getStatus());
	        }
	        nmUomRepo.saveAll(existingNmUom);
	        return existingNmUom.stream().map(this::mapToNmUomResponse).toList();
	    }

	    @Override
	    public NmUomResponse updateStatusUsingNmUomId(Long id) throws ResourceNotFoundException {
	        NmUom existingNmUom = this.findNmUomById(id);
	        existingNmUom.setStatus(!existingNmUom.getStatus());
	        nmUomRepo.save(existingNmUom);
	        return mapToNmUomResponse(existingNmUom);
	    }
}
