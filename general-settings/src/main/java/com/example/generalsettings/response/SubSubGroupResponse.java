package com.example.generalsettings.response;

import java.util.Date;
import java.util.List;

import com.example.generalsettings.entity.UpdateAuditHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubSubGroupResponse {
	private Long id;
	private String subSubGroupCode;
	private String subSubGroupName;
	private Boolean subSubGroupStatus;
	private MainGroupCodesResponse mainGroupCodes;
	private SubGroupCodesResponse subGroupCodes;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}