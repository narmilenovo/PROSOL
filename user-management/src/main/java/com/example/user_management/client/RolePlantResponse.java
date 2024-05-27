package com.example.user_management.client;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.example.user_management.client.plant.PlantResponse;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.entity.UpdateAuditHistory;

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
	private List<PrivilegeResponse> privileges;
	private String subRole;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
