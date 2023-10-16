package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.entity.DistributionChannel;
import com.example.sales_otherservice.entity.SalesOrganization;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.DistributionChannelRepository;
import com.example.sales_otherservice.repository.SalesOrganizationRepository;
import com.example.sales_otherservice.service.interfaces.DistributionChannelService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DistributionChannelServiceImpl implements DistributionChannelService {
    private final DistributionChannelRepository distributionChannelRepository;
    private final SalesOrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;

    @Override
    public DistributionChannelResponse saveDc(DistributionChannelRequest deliveringPlantRequest) throws ResourceFoundException, ResourceNotFoundException {
        String dcCode = deliveringPlantRequest.getDcCode();
        String dcName = deliveringPlantRequest.getDcName();
        boolean exists = distributionChannelRepository.existsByDcCodeOrDcName(dcCode, dcName);
        if (!exists) {

            DistributionChannel channel = modelMapper.map(deliveringPlantRequest, DistributionChannel.class);
            channel.setSalesOrganization(setSalesOrg(deliveringPlantRequest.getSalesOrganizationName()));
            DistributionChannel savedChannel = distributionChannelRepository.save(channel);
            return mapToDistributionChannelResponse(savedChannel);
        }
        throw new ResourceFoundException("Distributed Channel Already Exists");
    }

    private SalesOrganization setSalesOrg(String salesOrganizationName) throws ResourceNotFoundException {
        Optional<SalesOrganization> organization = organizationRepository.findBySoName(salesOrganizationName);
        if (organization.isEmpty()) {
            throw new ResourceNotFoundException("No organization Found");
        }
        return organization.get();
    }

    @Override
    public List<DistributionChannelResponse> getAllDc() {
        List<DistributionChannel> distributionChannels = distributionChannelRepository.findAll();
        return distributionChannels.stream().map(this::mapToDistributionChannelResponse).toList();
    }

    @Override
    public DistributionChannelResponse getDcById(Long id) throws ResourceNotFoundException {
        DistributionChannel channel = this.findDCById(id);
        return mapToDistributionChannelResponse(channel);
    }

    @Override
    public List<DistributionChannelResponse> findAllStatusTrue() {
        List<DistributionChannel> distributionChannels = distributionChannelRepository.findAllByDcStatusIsTrue();
        return distributionChannels.stream().map(this::mapToDistributionChannelResponse).toList();
    }

    @Override
    public DistributionChannelResponse updateDc(Long id, DistributionChannelRequest updateDistributionChannelRequest) throws ResourceNotFoundException, ResourceFoundException {
        String dcCode = updateDistributionChannelRequest.getDcCode();
        String dcName = updateDistributionChannelRequest.getDcName();
        DistributionChannel existingChannel = this.findDCById(id);
        boolean exists = distributionChannelRepository.existsByDcCodeAndIdNotOrDcNameAndIdNot(dcCode, id, dcName, id);
        if (!exists) {
            modelMapper.map(updateDistributionChannelRequest, existingChannel);
            existingChannel.setSalesOrganization(setSalesOrg(updateDistributionChannelRequest.getSalesOrganizationName()));
            DistributionChannel updatedChannel = distributionChannelRepository.save(existingChannel);
            return mapToDistributionChannelResponse(updatedChannel);
        }
        throw new ResourceFoundException("Distributed Channel Already Exists");
    }

    @Override
    public void deleteDcId(Long id) throws ResourceNotFoundException {
        DistributionChannel channel = this.findDCById(id);
        distributionChannelRepository.deleteById(channel.getId());
    }

    private DistributionChannelResponse mapToDistributionChannelResponse(DistributionChannel distributionChannel) {
        return modelMapper.map(distributionChannel, DistributionChannelResponse.class);
    }

    private DistributionChannel findDCById(Long id) throws ResourceNotFoundException {
        Optional<DistributionChannel> distributionChannel = distributionChannelRepository.findById(id);
        if (distributionChannel.isEmpty()) {
            throw new ResourceNotFoundException("Distribution Channel not found with this Id");
        }
        return distributionChannel.get();
    }
}
