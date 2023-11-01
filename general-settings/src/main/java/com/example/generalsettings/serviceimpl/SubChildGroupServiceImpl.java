package com.example.generalsettings.serviceimpl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.entity.SubChildGroup;
import com.example.generalsettings.entity.SubMainGroup;
import com.example.generalsettings.repo.MainGroupCodesRepo;
import com.example.generalsettings.repo.SubChildGroupRepo;
import com.example.generalsettings.repo.SubMainGroupRepo;
import com.example.generalsettings.request.SubChildGroupRequest;
import com.example.generalsettings.response.SubChildGroupResponse;
import com.example.generalsettings.service.SubChildGroupService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class SubChildGroupServiceImpl implements SubChildGroupService {
	 private static final String SUB_CHILD_GROUP_NOT_FOUND_MESSAGE = null;

	    private final SubChildGroupRepo subChildGroupRepo;
	    private final MainGroupCodesRepo mainGroupCodesRepo;
	    private final SubMainGroupRepo subMainGroupRepo;

	    private final ModelMapper modelMapper;

	    @Override
	    public List<SubChildGroupResponse> getAllSubChildGroup() {
	        List<SubChildGroup> subChildGroup = subChildGroupRepo.findAll();
	        return subChildGroup.stream().map(this::mapToSubChildGroupResponse).toList();
	    }

	    @Override
	    public SubChildGroupResponse updateSubChildGroup(Long id, SubChildGroupRequest subChildGroupRequest) throws ResourceNotFoundException, AlreadyExistsException {

	        Optional<SubChildGroup> existSubChildGroup = subChildGroupRepo.findByTitle(subChildGroupRequest.getTitle());
	        if (existSubChildGroup.isPresent() && !existSubChildGroup.get().getTitle().equals(subChildGroupRequest.getTitle())) {
	            throw new AlreadyExistsException("SubChildGroup with this name already exists");
	        } else {

	            SubChildGroup existSubChildGroup1 = this.findSubChildGroupById(id);
	            modelMapper.map(subChildGroupRequest, existSubChildGroup1);
				existSubChildGroup1.setSubId(null);
	            subChildGroupRepo.save(existSubChildGroup1);
	            return mapToSubChildGroupResponse(existSubChildGroup1);
	        }
	    }

	    @Override
	    public void deleteSubChildGroup(Long id) throws ResourceNotFoundException {
	        SubChildGroup subChildGroup = this.findSubChildGroupById(id);
	        subChildGroupRepo.deleteById(subChildGroup.getSubId());
	    }

	    @Override
	    public SubChildGroupResponse saveSubChildGroup(SubChildGroupRequest subChildGroupRequest) throws  AlreadyExistsException {

	        Optional<SubChildGroup> existSubChildGroup = subChildGroupRepo.findByTitle(subChildGroupRequest.getTitle());
	        if (existSubChildGroup.isPresent()) {
	            throw new AlreadyExistsException("SubChildGroup with this name already exists");
	        } else {
	            SubChildGroup subChildGroup = modelMapper.map(subChildGroupRequest, SubChildGroup.class);
	            subChildGroup.setMainGroupCodesId(setToString(subChildGroupRequest.getMainGroupCodesId()));
	            subChildGroup.setSubMainGroupId(setToString1(subChildGroupRequest.getSubMainGroupId()));
	            SubChildGroup saved=subChildGroupRepo.save(subChildGroup);
	            return mapToSubChildGroupResponse(saved);
	        }
	    }

	    private MainGroupCodes setToString(Long mainId) {
	        Optional<MainGroupCodes> fetchplantOptional = mainGroupCodesRepo.findById(mainId);
	        return fetchplantOptional.orElse(null);

	    }

	    private SubMainGroup setToString1(Long subId) {
	        Optional<SubMainGroup> fetchStorageOptional1 = subMainGroupRepo.findById(subId);
	        return fetchStorageOptional1.orElse(null);

	    }

	    private SubChildGroupResponse mapToSubChildGroupResponse(SubChildGroup subChildGroup) {
	        return modelMapper.map(subChildGroup, SubChildGroupResponse.class);
	    }


	    private SubChildGroup findSubChildGroupById(Long id) throws ResourceNotFoundException {
	        Optional<SubChildGroup> subChildGroup = subChildGroupRepo.findById(id);
	        if (subChildGroup.isEmpty()) {
	            throw new ResourceNotFoundException(SUB_CHILD_GROUP_NOT_FOUND_MESSAGE);
	        }
	        return subChildGroup.get();
	    }

	    @Override
	    public SubChildGroupResponse getSubChildGroupById(Long id) throws ResourceNotFoundException {
	        SubChildGroup subChildGroup = this.findSubChildGroupById(id);
	        return mapToSubChildGroupResponse(subChildGroup);
	    }

	    @Override
	    public List<SubChildGroupResponse> updateBulkStatusSubChildGroupId(List<Long> id) {
	        List<SubChildGroup> existingSubChildGroup = subChildGroupRepo.findAllById(id);
	        for (SubChildGroup subChildGroup : existingSubChildGroup) {
	            subChildGroup.setStatus(!subChildGroup.getStatus());
	        }
	        subChildGroupRepo.saveAll(existingSubChildGroup);
	        return existingSubChildGroup.stream().map(this::mapToSubChildGroupResponse).toList();
	    }
	    @Override
	    public SubChildGroupResponse updateStatusUsingSubChildGroupId(Long id) throws ResourceNotFoundException {
	        SubChildGroup existingSubChildGroup = this.findSubChildGroupById(id);
	        existingSubChildGroup.setStatus(!existingSubChildGroup.getStatus());
	        subChildGroupRepo.save(existingSubChildGroup);
	        return mapToSubChildGroupResponse(existingSubChildGroup);
	    }
}
