package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.DistributionChannel;
import com.example.sales_otherservice.entity.SalesOrganization;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.DistributionChannelRepository;
import com.example.sales_otherservice.repository.SalesOrganizationRepository;
import com.example.sales_otherservice.service.interfaces.DistributionChannelService;
import com.example.sales_otherservice.utils.Helpers;

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
		Helpers.inputTitleCase(deliveringPlantRequest);
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
			SalesOrganization salesOrganization = findSalesOrgById(deliveringPlantRequest.getSalesOrganizationId());
			channel.setSalesOrganization(salesOrganization);
			DistributionChannel savedChannel = distributionChannelRepository.save(channel);
			return mapToDistributionChannelResponse(savedChannel);
		}
		throw new ResourceFoundException("Distributed Channel Already Exists");
	}

	private SalesOrganization findSalesOrgById(Long salesOrganizationId) throws ResourceNotFoundException {
		Optional<SalesOrganization> organization = organizationRepository.findById(salesOrganizationId);
		if (organization.isEmpty()) {
			throw new ResourceNotFoundException("No organization Found");
		}
		return organization.get();
	}

	@Override
	public DistributionChannelResponse getDcById(Long id) throws ResourceNotFoundException {
		DistributionChannel channel = this.findDCById(id);
		return mapToDistributionChannelResponse(channel);
	}

	@Override
	public List<DistributionChannelResponse> getAllDc() {
		List<DistributionChannel> distributionChannels = distributionChannelRepository.findAll();
		return distributionChannels.stream().sorted(Comparator.comparing(DistributionChannel::getId))
				.map(this::mapToDistributionChannelResponse).toList();
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
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateDistributionChannelRequest);
		String dcCode = updateDistributionChannelRequest.getDcCode();
		String dcName = updateDistributionChannelRequest.getDcName();
		DistributionChannel existingChannel = this.findDCById(id);
		boolean exists = distributionChannelRepository.existsByDcCodeAndIdNotOrDcNameAndIdNot(dcCode, id, dcName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingChannel.getDcCode().equals(dcCode)) {
				auditFields.add(new AuditFields(null, "Dc Code", existingChannel.getDcCode(), dcCode));
				existingChannel.setDcCode(dcCode);
			}
			if (!existingChannel.getDcName().equals(dcName)) {
				auditFields.add(new AuditFields(null, "Dc Name", existingChannel.getDcName(), dcName));
				existingChannel.setDcName(dcName);
			}
			if (updateDistributionChannelRequest.getDcStatus() != null
					&& !updateDistributionChannelRequest.getDcStatus().equals(existingChannel.getDcStatus())) {
				auditFields.add(new AuditFields(null, "Dc Status", existingChannel.getDcStatus(),
						updateDistributionChannelRequest.getDcStatus()));
				existingChannel.setDcStatus(updateDistributionChannelRequest.getDcStatus());

			}
			if (!existingChannel.getSalesOrganization()
					.equals(findSalesOrgById(updateDistributionChannelRequest.getSalesOrganizationId()))) {
				auditFields.add(new AuditFields(null, "Sales Organization", existingChannel.getSalesOrganization(),
						updateDistributionChannelRequest.getSalesOrganizationId()));
				SalesOrganization salesOrganization = findSalesOrgById(
						updateDistributionChannelRequest.getSalesOrganizationId());
				existingChannel.setSalesOrganization(salesOrganization);

			}
			if (!existingChannel.getDynamicFields().equals(updateDistributionChannelRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateDistributionChannelRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingChannel.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingChannel.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingChannel.updateAuditHistory(auditFields);
			DistributionChannel updatedChannel = distributionChannelRepository.save(existingChannel);
			return mapToDistributionChannelResponse(updatedChannel);
		}
		throw new ResourceFoundException("Distributed Channel Already Exists");
	}

	@Override
	public DistributionChannelResponse updateDcStatus(Long id) throws ResourceNotFoundException {
		DistributionChannel existingChannel = this.findDCById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingChannel.getDcStatus() != null) {
			auditFields.add(
					new AuditFields(null, "Dc Status", existingChannel.getDcStatus(), !existingChannel.getDcStatus()));
			existingChannel.setDcStatus(!existingChannel.getDcStatus());
		}
		existingChannel.updateAuditHistory(auditFields);
		distributionChannelRepository.save(existingChannel);
		return mapToDistributionChannelResponse(existingChannel);
	}

	@Override
	public List<DistributionChannelResponse> updateBatchDcStatus(List<Long> ids) throws ResourceNotFoundException {
		List<DistributionChannel> channels = this.findAllDcById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		channels.forEach(existingChannel -> {
			if (existingChannel.getDcStatus() != null) {
				auditFields.add(new AuditFields(null, "Dc Status", existingChannel.getDcStatus(),
						!existingChannel.getDcStatus()));
				existingChannel.setDcStatus(!existingChannel.getDcStatus());
			}
			existingChannel.updateAuditHistory(auditFields);

		});
		distributionChannelRepository.saveAll(channels);
		return channels.stream().sorted(Comparator.comparing(DistributionChannel::getId))
				.map(this::mapToDistributionChannelResponse).toList();
	}

	@Override
	public void deleteDcId(Long id) throws ResourceNotFoundException {
		DistributionChannel channel = this.findDCById(id);
		distributionChannelRepository.deleteById(channel.getId());
	}

	@Override
	public void deleteBatchDc(List<Long> ids) throws ResourceNotFoundException {
		this.findAllDcById(ids);
		distributionChannelRepository.deleteAllByIdInBatch(ids);
	}

	private DistributionChannelResponse mapToDistributionChannelResponse(DistributionChannel distributionChannel) {
		return modelMapper.map(distributionChannel, DistributionChannelResponse.class);
	}

	private DistributionChannel findDCById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<DistributionChannel> distributionChannel = distributionChannelRepository.findById(id);
		if (distributionChannel.isEmpty()) {
			throw new ResourceNotFoundException("Distribution Channel not found with this Id");
		}
		return distributionChannel.get();
	}

	private List<DistributionChannel> findAllDcById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<DistributionChannel> channels = distributionChannelRepository.findAllById(ids);

		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> channels.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Distribution Channel with IDs " + missingIds + " not found.");
		}
		return channels;
	}

}
