package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.repo.MainGroupCodesRepo;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;
import com.example.generalsettings.service.MainGroupCodesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainGroupCodesServiceImpl implements MainGroupCodesService{

    private final MainGroupCodesRepo mainGroupCodesRepo;

    private final ModelMapper modelMapper;

    public static final String MAIN_GROUP_CODE_NOT_FOUND_MESSAGE = null;


    @Override
    public List<MainGroupCodesResponse> getAllMainGroupCodes() {
        List<MainGroupCodes> mainGroupCodes = mainGroupCodesRepo.findAll();
        return mainGroupCodes.stream().map(this::mapToMainGroupCodesResponse).toList();
    }

    @Override
    public MainGroupCodesResponse updateMainGroupCodes(Long id, MainGroupCodesRequest mainGroupCodesRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<MainGroupCodes> existMainGroupCodesName = mainGroupCodesRepo.findByMainGroupName(mainGroupCodesRequest.getMainGroupName());
        if (existMainGroupCodesName.isPresent() && !existMainGroupCodesName.get().getMainGroupName().equals(mainGroupCodesRequest.getMainGroupName())) {
            throw new AlreadyExistsException("MainGroupCodes with this name already exists");
        } else {
            MainGroupCodes existingMainGroupCodes = this.findMainGroupCodesById(id);
            modelMapper.map(mainGroupCodesRequest, existingMainGroupCodes);
            mainGroupCodesRepo.save(existingMainGroupCodes);
            return mapToMainGroupCodesResponse(existingMainGroupCodes);
        }
    }

    public void deleteMainGroupCodes(Long id) throws ResourceNotFoundException {
        MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(id);
        mainGroupCodesRepo.deleteById(mainGroupCodes.getId());
    }

    @Override
    public MainGroupCodesResponse saveMainGroupCodes(MainGroupCodesRequest mainGroupCodesRequest) throws  AlreadyExistsException {

        Optional<MainGroupCodes> existMainGroupCodesName = mainGroupCodesRepo.findByMainGroupName(mainGroupCodesRequest.getMainGroupName());
        if (existMainGroupCodesName.isPresent() ) {
            throw new AlreadyExistsException("MainGroupCodes with this name already exists");
        } else {
            MainGroupCodes mainGroupCodes = modelMapper.map(mainGroupCodesRequest, MainGroupCodes.class);
            mainGroupCodesRepo.save(mainGroupCodes);
            return mapToMainGroupCodesResponse(mainGroupCodes);
        }
    }


    private MainGroupCodesResponse mapToMainGroupCodesResponse(MainGroupCodes mainGroupCodes) {
        return modelMapper.map(mainGroupCodes, MainGroupCodesResponse.class);
    }

    private MainGroupCodes findMainGroupCodesById(Long id) throws ResourceNotFoundException {
        Optional<MainGroupCodes> mainGroupCodes = mainGroupCodesRepo.findById(id);
        if (mainGroupCodes.isEmpty()) {
            throw new ResourceNotFoundException(MAIN_GROUP_CODE_NOT_FOUND_MESSAGE);
        }
        return mainGroupCodes.get();
    }



    @Override
    public MainGroupCodesResponse getMainGroupCodesById(Long id) throws ResourceNotFoundException {
        MainGroupCodes mainGroupCodes = this.findMainGroupCodesById(id);
        return mapToMainGroupCodesResponse(mainGroupCodes);
    }

    @Override
    public List<MainGroupCodesResponse> updateBulkStatusMainGroupCodesId(List<Long> id) {
        List<MainGroupCodes> existingMainGroupCodes = mainGroupCodesRepo.findAllById(id);
        for (MainGroupCodes mainGroupCodes : existingMainGroupCodes) {
            mainGroupCodes.setMainGroupStatus(!mainGroupCodes.getMainGroupStatus());
        }
        mainGroupCodesRepo.saveAll(existingMainGroupCodes);
        return existingMainGroupCodes.stream().map(this::mapToMainGroupCodesResponse).toList();
    }

    @Override
    public MainGroupCodesResponse updateStatusUsingMainGroupCodesId(Long id) throws ResourceNotFoundException {
        MainGroupCodes existingMainGroupCodes = this.findMainGroupCodesById(id);
        existingMainGroupCodes.setMainGroupStatus(!existingMainGroupCodes.getMainGroupStatus());
        mainGroupCodesRepo.save(existingMainGroupCodes);
        return mapToMainGroupCodesResponse(existingMainGroupCodes);
    }
}
