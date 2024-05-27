package com.example.user_management.dto.response;

import lombok.Data;

@Data
public class AssigneeResponse {
	private Long id;
	private RoleResponse role;
	private String subUser;

}
