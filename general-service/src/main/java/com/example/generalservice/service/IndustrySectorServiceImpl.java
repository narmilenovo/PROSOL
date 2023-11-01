package com.example.generalservice.service;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.entity.IndustrySector;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.IndustrySectorRepository;
import com.example.generalservice.service.interfaces.IndustrySectorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndustrySectorServiceImpl implements IndustrySectorService {
    private final IndustrySectorRepository sectorRepository;
    private final ModelMapper modelMapper;

    @Override
    public IndustrySectorResponse saveSector(IndustrySectorRequest industrySectorRequest) throws ResourceFoundException {
        String sectorCode = industrySectorRequest.getSectorCode();
        String sectorName = industrySectorRequest.getSectorName();
        boolean exists = sectorRepository.existsBySectorCodeOrSectorName(sectorCode, sectorName);
        if (!exists) {

            IndustrySector industrySector = modelMapper.map(industrySectorRequest, IndustrySector.class);
            IndustrySector savedSector = sectorRepository.save(industrySector);
            return mapToIndustrySectorResponse(savedSector);
        }
        throw new ResourceFoundException("Industry Sector Already Exist");
    }

    @Override
    public List<IndustrySectorResponse> getAllSector() {
        List<IndustrySector> sectorResponses = sectorRepository.findAll();
        return sectorResponses.stream().map(this::mapToIndustrySectorResponse).toList();
    }

    @Override
    public IndustrySectorResponse getSectorById(Long id) throws ResourceNotFoundException {
        IndustrySector industrySector = this.findSectorById(id);
        return mapToIndustrySectorResponse(industrySector);

    }

    @Override
    public List<IndustrySectorResponse> findAllStatusTrue() {
        List<IndustrySector> sectorResponses = sectorRepository.findAllBySectorStatusIsTrue();
        return sectorResponses.stream().map(this::mapToIndustrySectorResponse).toList();
    }

    @Override
    public IndustrySectorResponse updateSector(Long id, IndustrySectorRequest updateindustrysectorrequest) throws ResourceNotFoundException, ResourceFoundException {
        String sectorCode = updateindustrysectorrequest.getSectorCode();
        String sectorName = updateindustrysectorrequest.getSectorName();
        IndustrySector existingIndustrySector = this.findSectorById(id);
        boolean exists = sectorRepository.existsBySectorCodeAndIdNotOrSectorNameAndIdNot(sectorCode, id, sectorName, id);
        if (!exists) {
            modelMapper.map(updateindustrysectorrequest, existingIndustrySector);
            IndustrySector updatedIndustrySector = sectorRepository.save(existingIndustrySector);
            return mapToIndustrySectorResponse(updatedIndustrySector);
        }
        throw new ResourceFoundException("Industry Sector Already Exist");
    }

    @Override
    public void deleteSectorId(Long id) throws ResourceNotFoundException {
        IndustrySector industrySector = this.findSectorById(id);
        sectorRepository.deleteById(industrySector.getId());
    }

    private IndustrySectorResponse mapToIndustrySectorResponse(IndustrySector industrySector) {
        return modelMapper.map(industrySector, IndustrySectorResponse.class);
    }

    private IndustrySector findSectorById(Long id) throws ResourceNotFoundException {
        Optional<IndustrySector> sector = sectorRepository.findById(id);
        if (sector.isEmpty()) {
            throw new ResourceNotFoundException("No Industry Sector found with this Id");
        }
        return sector.get();
    }
}
