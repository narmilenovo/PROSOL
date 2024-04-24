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
public class SubGroupCodesResponse {
	private Long id;
	private String subGroupCode;
	private String subGroupName;
	private Boolean subGroupStatus;

	private MainGroupCodesResponse mainGroupCodes;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
