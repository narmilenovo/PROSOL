package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.entity.LoadingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.LoadingGroupRepository;
import com.example.sales_otherservice.service.interfaces.LoadingGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadingGroupServiceImpl implements LoadingGroupService {
    private final LoadingGroupRepository loadingGroupRepository;
    private final ModelMapper modelMapper;

    @Override
    public LoadingGroupResponse saveLg(LoadingGroupRequest loadingGroupRequest) throws ResourceFoundException {
        String lgCode = loadingGroupRequest.getLgCode();
        String lgName = loadingGroupRequest.getLgName();
        boolean exists = loadingGroupRepository.existsByLgCodeOrLgName(lgCode, lgName);
        if (!exists) {

            LoadingGroup loadingGroup = modelMapper.map(loadingGroupRequest, LoadingGroup.class);
            LoadingGroup savedGroup = loadingGroupRepository.save(loadingGroup);
            return mapToLoadingGroupResponse(savedGroup);
        }
        throw new ResourceFoundException("Loading Group already Exists !!!");
    }

    @Override
    @Cacheable("lg")
    public List<LoadingGroupResponse> getAllLg() {
        List<LoadingGroup> loadingGroups = loadingGroupRepository.findAll();
        return loadingGroups.stream()
                .sorted(Comparator.comparing(LoadingGroup::getId))
                .map(this::mapToLoadingGroupResponse)
                .toList();
    }

    @Override
    @Cacheable("lg")
    public LoadingGroupResponse getLgById(Long id) throws ResourceNotFoundException {
        LoadingGroup loadingGroup = this.findLgById(id);
        return mapToLoadingGroupResponse(loadingGroup);
    }

    @Override
    @Cacheable("lg")
    public List<LoadingGroupResponse> findAllStatusTrue() {
        List<LoadingGroup> loadingGroups = loadingGroupRepository.findAllByLgStatusIsTrue();
        return loadingGroups.stream()
                .sorted(Comparator.comparing(LoadingGroup::getId))
                .map(this::mapToLoadingGroupResponse)
                .toList();
    }

    @Override
    public LoadingGroupResponse updateLg(Long id, LoadingGroupRequest updateLoadingGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        String lgCode = updateLoadingGroupRequest.getLgCode();
        String lgName = updateLoadingGroupRequest.getLgName();
        LoadingGroup existingLoadingGroup = this.findLgById(id);
        boolean exists = loadingGroupRepository.existsByLgCodeAndIdNotOrLgNameAndIdNot(lgCode, id, lgName, id);
        if (!exists) {
            modelMapper.map(updateLoadingGroupRequest, existingLoadingGroup);
            LoadingGroup updatedLoadingGroup = loadingGroupRepository.save(existingLoadingGroup);
            return mapToLoadingGroupResponse(updatedLoadingGroup);
        }
        throw new ResourceFoundException("Loading Group already Exists !!!");
    }

    @Override
    public void deleteLgById(Long id) throws ResourceNotFoundException {
        LoadingGroup loadingGroup = this.findLgById(id);
        loadingGroupRepository.deleteById(loadingGroup.getId());
    }

    private LoadingGroupResponse mapToLoadingGroupResponse(LoadingGroup loadingGroup) {
        return modelMapper.map(loadingGroup, LoadingGroupResponse.class);
    }

    private LoadingGroup findLgById(Long id) throws ResourceNotFoundException {
        Optional<LoadingGroup> loadingGroup = loadingGroupRepository.findById(id);
        if (loadingGroup.isEmpty()) {
            throw new ResourceNotFoundException("Loading Group not found with this Id");
        }
        return loadingGroup.get();
    }
}
