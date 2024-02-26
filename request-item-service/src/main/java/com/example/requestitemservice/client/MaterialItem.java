package com.example.requestitemservice.client;

import java.util.Date;
import java.util.List;

import com.example.requestitemservice.client.general.IndustrySectorResponse;
import com.example.requestitemservice.client.general.MaterialTypeResponse;
import com.example.requestitemservice.client.plant.PlantResponse;
import com.example.requestitemservice.client.plant.StorageLocationResponse;
import com.example.requestitemservice.client.salesothers.MaterialStrategicGroupResponse;
import com.example.requestitemservice.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaterialItem {

	private Long id;
	private PlantResponse plant;
	private StorageLocationResponse storageLocation;
	private MaterialTypeResponse materialType;
	private IndustrySectorResponse industrySector;
	private MaterialStrategicGroupResponse materialGroup;
	private String source;
	private String attachment;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;

}
