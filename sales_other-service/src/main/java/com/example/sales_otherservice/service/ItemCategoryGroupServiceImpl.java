package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.entity.ItemCategoryGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.ItemCategoryGroupRepository;
import com.example.sales_otherservice.service.interfaces.ItemCategoryGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemCategoryGroupServiceImpl implements ItemCategoryGroupService {
    private final ItemCategoryGroupRepository itemCategoryGroupRepository;
    private final ModelMapper modelMapper;

    @Override
    public ItemCategoryGroupResponse saveIcg(ItemCategoryGroupRequest itemCategoryGroupRequest) throws ResourceFoundException {
        String icgCode = itemCategoryGroupRequest.getIcgCode();
        String icgName = itemCategoryGroupRequest.getIcgName();
        boolean exists = itemCategoryGroupRepository.existsByIcgCodeOrIcgName(icgCode, icgName);
        if (!exists) {

            ItemCategoryGroup categoryGroup = modelMapper.map(itemCategoryGroupRequest, ItemCategoryGroup.class);
            ItemCategoryGroup savedGroup = itemCategoryGroupRepository.save(categoryGroup);
            return mapToItemCategoryGroupResponse(savedGroup);
        }
        throw new ResourceFoundException("Item Category Group Already Exist");
    }

    @Override
    @Cacheable("icg")
    public List<ItemCategoryGroupResponse> getAllIcg() {
        List<ItemCategoryGroup> categoryGroups = itemCategoryGroupRepository.findAll();
        return categoryGroups.stream()
                .sorted(Comparator.comparing(ItemCategoryGroup::getId))
                .map(this::mapToItemCategoryGroupResponse)
                .toList();
    }

    @Override
    @Cacheable("icg")
    public ItemCategoryGroupResponse getIcgById(Long id) throws ResourceNotFoundException {
        ItemCategoryGroup itemCategoryGroup = this.findIcgById(id);
        return mapToItemCategoryGroupResponse(itemCategoryGroup);
    }

    @Override
    @Cacheable("icg")
    public List<ItemCategoryGroupResponse> findAllStatusTrue() {
        List<ItemCategoryGroup> categoryGroups = itemCategoryGroupRepository.findAllByIcgStatusIsTrue();
        return categoryGroups.stream()
                .sorted(Comparator.comparing(ItemCategoryGroup::getId))
                .map(this::mapToItemCategoryGroupResponse)
                .toList();
    }

    @Override
    public ItemCategoryGroupResponse updateIcg(Long id, ItemCategoryGroupRequest updateItemCategoryGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        String icgCode = updateItemCategoryGroupRequest.getIcgCode();
        String icgName = updateItemCategoryGroupRequest.getIcgName();
        ItemCategoryGroup existingCategoryGroup = this.findIcgById(id);
        boolean exists = itemCategoryGroupRepository.existsByIcgCodeAndIdNotOrIcgNameAndIdNot(icgCode, id, icgName, id);
        if (!exists) {
            modelMapper.map(updateItemCategoryGroupRequest, existingCategoryGroup);
            ItemCategoryGroup updatedGroup = itemCategoryGroupRepository.save(existingCategoryGroup);
            return mapToItemCategoryGroupResponse(updatedGroup);
        }
        throw new ResourceFoundException("Item Category Group Already Exist");
    }

    @Override
    public void deleteIcgById(Long id) throws ResourceNotFoundException {
        ItemCategoryGroup categoryGroup = this.findIcgById(id);
        itemCategoryGroupRepository.deleteById(categoryGroup.getId());
    }

    private ItemCategoryGroupResponse mapToItemCategoryGroupResponse(ItemCategoryGroup itemCategoryGroup) {
        return modelMapper.map(itemCategoryGroup, ItemCategoryGroupResponse.class);
    }

    private ItemCategoryGroup findIcgById(Long id) throws ResourceNotFoundException {
        Optional<ItemCategoryGroup> categoryGroup = itemCategoryGroupRepository.findById(id);
        if (categoryGroup.isEmpty()) {
            throw new ResourceNotFoundException("Item Category Group not found with this Id");
        }
        return categoryGroup.get();
    }
}