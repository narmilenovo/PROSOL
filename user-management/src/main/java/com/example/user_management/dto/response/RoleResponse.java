package com.example.user_management.dto.response;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.example.user_management.entity.UpdateAuditHistory;

import lombok.Data;

@Data
public class RoleResponse {
	private Long id;
	private String name;
	private String description;
	private Long plantId;
	private Boolean status;
	private Set<PrivilegeResponse> privileges;
	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}