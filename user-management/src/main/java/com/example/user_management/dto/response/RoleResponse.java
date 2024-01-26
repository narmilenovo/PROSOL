package com.example.user_management.dto.response;

import java.util.Date;
import java.util.Set;

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
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
