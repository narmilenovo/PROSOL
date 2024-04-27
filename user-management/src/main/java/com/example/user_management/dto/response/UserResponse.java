package com.example.user_management.dto.response;

import java.util.Date;
import java.util.List;

import com.example.user_management.entity.UpdateAuditHistory;

import lombok.Data;

@Data
public class UserResponse {
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phone;
	private String avatar;
	private String business;
	private Long departmentId;
	private List<Long> plantId;
	private Boolean status;
	private List<RoleResponse> roles;
	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
