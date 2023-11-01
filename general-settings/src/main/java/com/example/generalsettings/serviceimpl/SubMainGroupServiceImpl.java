package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.entity.SubMainGroup;
import com.example.generalsettings.repo.MainGroupCodesRepo;
import com.example.generalsettings.repo.SubMainGroupRepo;
import com.example.generalsettings.request.SubMainGroupRequest;
import com.example.generalsettings.response.SubMainGroupResponse;
import com.example.generalsettings.service.SubMainGroupService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SubMainGroupServiceImpl implements SubMainGroupService{
	private static final String PROFIT_CENTER_NOT_FOUND_MESSAGE = null;

    private final SubMainGroupRepo subMainGroupRepo;

    private final MainGroupCodesRepo mainGroupCodesRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<SubMainGroupResponse> getAllSubMainGroup() {
        List<SubMainGroup> subMainGroup = subMainGroupRepo.findAll();
        return subMainGroup.stream().map(this::mapToSubMainGroupResponse).toList();
    }

    @Override
    public SubMainGroupResponse updateSubMainGroup(Long id, SubMainGroupRequest subMainGroupRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<SubMainGroup> existSubMainGroupName = subMainGroupRepo.findBySubMainGroupTitle(subMainGroupRequest.getSubMainGroupTitle());
        if (existSubMainGroupName.isPresent()&& !existSubMainGroupName.get().getSubMainGroupTitle().equals(subMainGroupRequest.getSubMainGroupTitle())) {
            throw new AlreadyExistsException("SubMainGroup with this name already exists");
        } else {

            SubMainGroup existingSubMainGroup = this.findSubMainGroupById(id);
            modelMapper.map(subMainGroupRequest, existingSubMainGroup);
            existingSubMainGroup.setMainGroupCodesId(setToMainGroupCodes(subMainGroupRequest.getMainGroupCodesId()));

            subMainGroupRepo.save(existingSubMainGroup);
            return mapToSubMainGroupResponse(existingSubMainGroup);
        }
    }

    public void deleteSubMainGroup(Long id) throws ResourceNotFoundException {
        SubMainGroup subMainGroup = this.findSubMainGroupById(id);
        subMainGroupRepo.deleteById(subMainGroup.getId());
    }

    @Override
    public SubMainGroupResponse saveSubMainGroup(SubMainGroupRequest subMainGroupRequest) throws  AlreadyExistsException {

        Optional<SubMainGroup> existSubMainGroupName = subMainGroupRepo.findBySubMainGroupTitle(subMainGroupRequest.getSubMainGroupTitle());
        if (existSubMainGroupName.isPresent()) {
            throw new AlreadyExistsException("SubMainGroup with this name already exists");
        } else {
            SubMainGroup subMainGroup = modelMapper.map(subMainGroupRequest, SubMainGroup.class);
            subMainGroup.setMainGroupCodesId(setToMainGroupCodes(subMainGroupRequest.getMainGroupCodesId()));
            subMainGroup.setId(null);
            subMainGroupRepo.save(subMainGroup);
            return mapToSubMainGroupResponse(subMainGroup);
        }
    }


    private MainGroupCodes setToMainGroupCodes(Long id) {
        Optional<MainGroupCodes> fetchplantOptional = mainGroupCodesRepo.findById(id);
        return fetchplantOptional.orElse(null);
    }

    private SubMainGroupResponse mapToSubMainGroupResponse(SubMainGroup subMainGroup) {
        return modelMapper.map(subMainGroup, SubMainGroupResponse.class);
    }


    private SubMainGroup findSubMainGroupById(Long id) throws ResourceNotFoundException {
        Optional<SubMainGroup> subMainGroup = subMainGroupRepo.findById(id);
        if (subMainGroup.isEmpty()) {
            throw new ResourceNotFoundException(PROFIT_CENTER_NOT_FOUND_MESSAGE);
        }
        return subMainGroup.get();
    }



    @Override
    public SubMainGroupResponse getSubMainGroupById(Long id) throws ResourceNotFoundException {
        SubMainGroup subMainGroup = this.findSubMainGroupById(id);
        return mapToSubMainGroupResponse(subMainGroup);
    }

    @Override
    public List<SubMainGroupResponse> updateBulkStatusSubMainGroupId(List<Long> id) {
        List<SubMainGroup> existingSubMainGroup = subMainGroupRepo.findAllById(id);
        for (SubMainGroup subMainGroup : existingSubMainGroup) {
            subMainGroup.setStatus(!subMainGroup.getStatus());
        }
        subMainGroupRepo.saveAll(existingSubMainGroup);
        return existingSubMainGroup.stream().map(this::mapToSubMainGroupResponse).toList();
    }

    @Override
    public SubMainGroupResponse updateStatusUsingSubMainGroupId(Long id) throws ResourceNotFoundException {
        SubMainGroup existingSubMainGroup = this.findSubMainGroupById(id);
        existingSubMainGroup.setStatus(!existingSubMainGroup.getStatus());
        subMainGroupRepo.save(existingSubMainGroup);
        return mapToSubMainGroupResponse(existingSubMainGroup);
    }

}
