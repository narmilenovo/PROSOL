package com.example.mrpdataservice.client;

import java.util.Date;
import java.util.Map;

import com.example.mrpdataservice.client.Plant.PlantResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MrpPlantResponse {
	private Long id;
	private String mrpControlCode;
	private String mrpControlName;
	private PlantResponse plant;
	private Boolean mrpControlStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;
}
