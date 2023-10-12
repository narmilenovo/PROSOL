package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.entity.TaxClassificationClass;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.TaxClassificationClassRepository;
import com.example.sales_otherservice.service.interfaces.TaxClassificationClassService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaxClassificationClassServiceImpl implements TaxClassificationClassService {
    private final TaxClassificationClassRepository taxClassificationClassRepository;
    private final ModelMapper modelMapper;

    @Override
    public TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest) {
        TaxClassificationClass classificationClass = modelMapper.map(taxClassificationClassRequest, TaxClassificationClass.class);
        TaxClassificationClass savedClassificationClass = taxClassificationClassRepository.save(classificationClass);
        return mapToTaxClassificationClassResponse(savedClassificationClass);
    }

    @Override
    public List<TaxClassificationClassResponse> getAllTcc() {
        List<TaxClassificationClass> taxClassificationClasses = taxClassificationClassRepository.findAll();
        return taxClassificationClasses.stream().map(this::mapToTaxClassificationClassResponse).toList();
    }

    @Override
    public TaxClassificationClassResponse getTccById(Long id) throws ResourceNotFoundException {
        TaxClassificationClass classificationClass = this.findTccById(id);
        return mapToTaxClassificationClassResponse(classificationClass);
    }

    @Override
    public List<TaxClassificationClassResponse> findAllStatusTrue() {
        List<TaxClassificationClass> taxClassificationClasses = taxClassificationClassRepository.findAllByTccStatusIsTrue();
        return taxClassificationClasses.stream().map(this::mapToTaxClassificationClassResponse).toList();
    }

    @Override
    public TaxClassificationClassResponse updateTcc(Long id, TaxClassificationClassRequest updateTaxClassificationClassRequest) throws ResourceNotFoundException, ResourceFoundException {
        String tccCode = updateTaxClassificationClassRequest.getTccCode();
        TaxClassificationClass existingClassificationClass = this.findTccById(id);
        boolean exists = taxClassificationClassRepository.existsByTccCode(tccCode);
        if (!exists) {
            modelMapper.map(updateTaxClassificationClassRequest, existingClassificationClass);
            TaxClassificationClass updatedClassificationClass = taxClassificationClassRepository.save(existingClassificationClass);
            return mapToTaxClassificationClassResponse(updatedClassificationClass);
        }
        throw new ResourceFoundException("Tax classification Already exists");
    }

    @Override
    public void deleteTccById(Long id) throws ResourceNotFoundException {
        TaxClassificationClass classificationClass = this.findTccById(id);
        taxClassificationClassRepository.deleteById(classificationClass.getId());

    }


    private TaxClassificationClassResponse mapToTaxClassificationClassResponse(TaxClassificationClass taxClassificationClass) {
        return modelMapper.map(taxClassificationClass, TaxClassificationClassResponse.class);
    }

    private TaxClassificationClass findTccById(Long id) throws ResourceNotFoundException {
        Optional<TaxClassificationClass> taxClassificationClass = taxClassificationClassRepository.findById(id);
        if (taxClassificationClass.isEmpty()) {
            throw new ResourceNotFoundException("Tax classification Class not found with this Id");
        }
        return taxClassificationClass.get();
    }
}
