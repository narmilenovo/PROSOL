package com.example.sales_otherservice.clients.Plant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantResponse {
	private Long id;
	private String plantCode;
	private String plantName;
	private Boolean plantStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields = new HashMap<>();
}
