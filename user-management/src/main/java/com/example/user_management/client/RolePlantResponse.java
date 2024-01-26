package com.example.user_management.client;

import java.util.Date;
import java.util.Set;

import com.example.user_management.dto.response.PrivilegeResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePlantResponse {
	private Long id;
	private String name;
	private String description;
	private PlantResponse plant;
	private Boolean status;
	private Set<PrivilegeResponse> privileges;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
