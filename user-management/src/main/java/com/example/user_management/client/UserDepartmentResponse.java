package com.example.user_management.client;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.example.user_management.client.plant.DepartmentResponse;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.UpdateAuditHistory;

import lombok.Data;

@Data
public class UserDepartmentResponse {
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String business;
	private DepartmentResponse department;
	private List<Long> plantId;
	private Boolean status;
	private Set<RoleResponse> roles;
	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
