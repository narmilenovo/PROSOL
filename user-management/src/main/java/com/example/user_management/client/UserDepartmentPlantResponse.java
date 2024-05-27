package com.example.user_management.client;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.example.user_management.client.plant.DepartmentResponse;
import com.example.user_management.client.plant.PlantResponse;
import com.example.user_management.dto.response.AssigneeResponse;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.UpdateAuditHistory;

import lombok.Data;

@Data
public class UserDepartmentPlantResponse {
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String avatar;
	private String business;
	private DepartmentResponse department;
	private List<PlantResponse> plants;
	private Boolean status;
	private List<AssigneeResponse> assignees;
	
	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
