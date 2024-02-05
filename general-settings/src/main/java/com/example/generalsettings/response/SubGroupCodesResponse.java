package com.example.generalsettings.response;

import java.util.Date;

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
	private MainGroupCodesResponse mainGroupCodesId;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
