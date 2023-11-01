package com.example.generalsettings.serviceimpl;


import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.repo.HsnRepo;
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;
import com.example.generalsettings.service.HsnService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HsnServiceImpl implements HsnService {
	  private final ModelMapper modelMapper;
		 private final HsnRepo hsnRepo;

		    public static final String HSN_NOT_FOUND_MESSAGE = null;

		    @Override
		    public List<HsnResponse> getAllHsn() {
		        List<Hsn> hsn = hsnRepo.findAll();
		        return hsn.stream().map(this::mapToHsnResponse).toList();
		    }

		    @Override
		    public HsnResponse updateHsn(Long id, HsnRequest hsnRequest) throws ResourceNotFoundException, AlreadyExistsException {

		        Optional<Hsn> existHsnName = hsnRepo.findByHsnCode(hsnRequest.getHsnCode());
		        if (existHsnName.isPresent() && !existHsnName.get().getHsnCode().equals(hsnRequest.getHsnCode())) {
		            throw new AlreadyExistsException("Hsn with this name already exists");
		        } else {
		            Hsn existingHsn = this.findHsnById(id);
		            modelMapper.map(hsnRequest, existingHsn);
		            hsnRepo.save(existingHsn);
		            return mapToHsnResponse(existingHsn);
		        }
		    }

		    public void deleteHsn(Long id) throws ResourceNotFoundException {
		        Hsn hsn = this.findHsnById(id);
		        hsnRepo.deleteById(hsn.getId());
		    }

		    @Override
		    public HsnResponse saveHsn(HsnRequest hsnRequest) throws  AlreadyExistsException {

		        Optional<Hsn> existHsnName = hsnRepo.findByHsnCode(hsnRequest.getHsnCode());
		        if (existHsnName.isPresent() ) {
		            throw new AlreadyExistsException("Hsn with this name already exists");
		        } else {
		            Hsn hsn = modelMapper.map(hsnRequest, Hsn.class);
		            hsnRepo.save(hsn);
		            return mapToHsnResponse(hsn);
		        }
		    }


		    private HsnResponse mapToHsnResponse(Hsn hsn) {
		        return modelMapper.map(hsn, HsnResponse.class);
		    }

		    private Hsn findHsnById(Long id) throws ResourceNotFoundException {
		        Optional<Hsn> hsn = hsnRepo.findById(id);
		        if (hsn.isEmpty()) {
		            throw new ResourceNotFoundException(HSN_NOT_FOUND_MESSAGE);
		        }
		        return hsn.get();
		    }



		    @Override
		    public HsnResponse getHsnById(Long id) throws ResourceNotFoundException {
		        Hsn hsn = this.findHsnById(id);
		        return mapToHsnResponse(hsn);
		    }

		    @Override
		    public List<HsnResponse> updateBulkStatusHsnId(List<Long> id) {
		        List<Hsn> existingHsn = hsnRepo.findAllById(id);
		        for (Hsn hsn : existingHsn) {
		            hsn.setHsnStatus(!hsn.getHsnStatus());
		        }
		        hsnRepo.saveAll(existingHsn);
		        return existingHsn.stream().map(this::mapToHsnResponse).toList();
		    }

		    @Override
		    public HsnResponse updateStatusUsingHsnId(Long id) throws ResourceNotFoundException {
		        Hsn existingHsn = this.findHsnById(id);
		        existingHsn.setHsnStatus(!existingHsn.getHsnStatus());
		        hsnRepo.save(existingHsn);
		        return mapToHsnResponse(existingHsn);
		    }
}
