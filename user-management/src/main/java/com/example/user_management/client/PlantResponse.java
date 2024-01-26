package com.example.user_management.client;

import java.util.Date;

import lombok.Data;

@Data
public class PlantResponse {
	private Long id;
	private String plantCode;
	private String plantName;
	private Boolean plantStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
