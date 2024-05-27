package com.example.user_management.dto.response;

import java.util.Date;
import java.util.List;

import com.example.user_management.entity.UpdateAuditHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeResponse {
	private Long id;
	private String name;
	private Boolean status;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
