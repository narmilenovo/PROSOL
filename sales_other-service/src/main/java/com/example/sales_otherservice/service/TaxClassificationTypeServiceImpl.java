package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.entity.TaxClassificationType;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.TaxClassificationTypeRepository;
import com.example.sales_otherservice.service.interfaces.TaxClassificationTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaxClassificationTypeServiceImpl implements TaxClassificationTypeService {
    private final TaxClassificationTypeRepository taxClassificationTypeRepository;
    private final ModelMapper modelMapper;

    @Override
    public TaxClassificationTypeResponse saveTct(TaxClassificationTypeRequest taxClassificationClassRequest) throws ResourceFoundException {
        String tctCode = taxClassificationClassRequest.getTctCode();
        String tctName = taxClassificationClassRequest.getTctName();
        boolean exists = taxClassificationTypeRepository.existsByTctCodeOrTctName(tctCode, tctName);
        if (!exists) {

            TaxClassificationType classificationType = modelMapper.map(taxClassificationClassRequest, TaxClassificationType.class);
            TaxClassificationType savedClassificationType = taxClassificationTypeRepository.save(classificationType);
            return mapToTaxClassificationClassResponse(savedClassificationType);
        }
        throw new ResourceFoundException("Tax classification Type Already exists");
    }

    @Override
    @Cacheable("tct")
    public List<TaxClassificationTypeResponse> getAllTct() {
        List<TaxClassificationType> classificationTypes = taxClassificationTypeRepository.findAll();
        return classificationTypes.stream()
                .sorted(Comparator.comparing(TaxClassificationType::getId))
                .map(this::mapToTaxClassificationClassResponse)
                .toList();
    }

    @Override
    @Cacheable("tct")
    public TaxClassificationTypeResponse getTctById(Long id) throws ResourceNotFoundException {
        TaxClassificationType classificationType = this.findTctById(id);
        return mapToTaxClassificationClassResponse(classificationType);
    }

    @Override
    @Cacheable("tct")
    public List<TaxClassificationTypeResponse> findAllStatusTrue() {
        List<TaxClassificationType> classificationTypes = taxClassificationTypeRepository.findAllByTctStatusIsTrue();
        return classificationTypes.stream()
                .sorted(Comparator.comparing(TaxClassificationType::getId))
                .map(this::mapToTaxClassificationClassResponse)
                .toList();
    }

    @Override
    public TaxClassificationTypeResponse updateTct(Long id, TaxClassificationTypeRequest updateTaxClassificationTypeRequest) throws ResourceNotFoundException, ResourceFoundException {
        String tctCode = updateTaxClassificationTypeRequest.getTctCode();
        String tctName = updateTaxClassificationTypeRequest.getTctName();
        TaxClassificationType existingClassificationType = this.findTctById(id);
        boolean exists = taxClassificationTypeRepository.existsByTctCodeAndIdNotOrTctNameAndIdNot(tctCode, id, tctName, id);
        if (!exists) {
            modelMapper.map(updateTaxClassificationTypeRequest, existingClassificationType);
            TaxClassificationType updateClassificationType = taxClassificationTypeRepository.save(existingClassificationType);
            return mapToTaxClassificationClassResponse(updateClassificationType);
        }
        throw new ResourceFoundException("Tax classification Type Already exists");
    }

    @Override
    public void deleteTctById(Long id) throws ResourceNotFoundException {
        TaxClassificationType classificationType = this.findTctById(id);
        taxClassificationTypeRepository.deleteById(classificationType.getId());
    }

    private TaxClassificationTypeResponse mapToTaxClassificationClassResponse(TaxClassificationType taxClassificationType) {
        return modelMapper.map(taxClassificationType, TaxClassificationTypeResponse.class);
    }

    private TaxClassificationType findTctById(Long id) throws ResourceNotFoundException {
        Optional<TaxClassificationType> classificationType = taxClassificationTypeRepository.findById(id);
        if (classificationType.isEmpty()) {
            throw new ResourceNotFoundException("Tax classification Type not found with this Id");
        }
        return classificationType.get();
    }
}
