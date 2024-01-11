package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DynamicClient;
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

@Service
@RequiredArgsConstructor
public class DistributionChannelServiceImpl implements DistributionChannelService {
	private final DistributionChannelRepository distributionChannelRepository;
	private final SalesOrganizationRepository organizationRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public DistributionChannelResponse saveDc(DistributionChannelRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String dcCode = deliveringPlantRequest.getDcCode();
		String dcName = deliveringPlantRequest.getDcName();
		boolean exists = distributionChannelRepository.existsByDcCodeOrDcName(dcCode, dcName);
		if (!exists) {
			DistributionChannel channel = modelMapper.map(deliveringPlantRequest, DistributionChannel.class);
			for (Map.Entry<String, Object> entryField : channel.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = DistributionChannel.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			channel.setId(null);
			channel.setSalesOrganization(setSalesOrg(deliveringPlantRequest.getSalesOrganizationId()));
			DistributionChannel savedChannel = distributionChannelRepository.save(channel);
			return mapToDistributionChannelResponse(savedChannel);
		}
		throw new ResourceFoundException("Distributed Channel Already Exists");
	}

	private SalesOrganization setSalesOrg(Long salesOrganizationId) throws ResourceNotFoundException {
		Optional<SalesOrganization> organization = organizationRepository.findById(salesOrganizationId);
		if (organization.isEmpty()) {
			throw new ResourceNotFoundException("No organization Found");
		}
		return organization.get();
	}

	@Override
	public List<DistributionChannelResponse> getAllDc() {
		List<DistributionChannel> distributionChannels = distributionChannelRepository.findAll();
		return distributionChannels.stream().sorted(Comparator.comparing(DistributionChannel::getId))
				.map(this::mapToDistributionChannelResponse).toList();
	}

	@Override
	public DistributionChannelResponse getDcById(Long id) throws ResourceNotFoundException {
		DistributionChannel channel = this.findDCById(id);
		return mapToDistributionChannelResponse(channel);
	}

	@Override
	public List<DistributionChannelResponse> findAllStatusTrue() {
		List<DistributionChannel> distributionChannels = distributionChannelRepository.findAllByDcStatusIsTrue();
		return distributionChannels.stream().sorted(Comparator.comparing(DistributionChannel::getId))
				.map(this::mapToDistributionChannelResponse).toList();
	}

	@Override
	public DistributionChannelResponse updateDc(Long id, DistributionChannelRequest updateDistributionChannelRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		String dcCode = updateDistributionChannelRequest.getDcCode();
		String dcName = updateDistributionChannelRequest.getDcName();
		DistributionChannel existingChannel = this.findDCById(id);
		boolean exists = distributionChannelRepository.existsByDcCodeAndIdNotOrDcNameAndIdNot(dcCode, id, dcName, id);
		if (!exists) {
			modelMapper.map(updateDistributionChannelRequest, existingChannel);
			for (Map.Entry<String, Object> entryField : existingChannel.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = DistributionChannel.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			existingChannel.setId(id);
			existingChannel
					.setSalesOrganization(setSalesOrg(updateDistributionChannelRequest.getSalesOrganizationId()));
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

	@Override
	public void deleteBatchDc(List<Long> ids) {
		distributionChannelRepository.deleteAllByIdInBatch(ids);
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
