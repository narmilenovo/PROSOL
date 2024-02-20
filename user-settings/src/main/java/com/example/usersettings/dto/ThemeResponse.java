package com.example.usersettings.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.example.usersettings.entity.UpdateAuditHistory;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ThemeResponse {
	private Long id;
	private String name;
	private String primaryColor;
	private String secondaryColor;
	private String tertiaryColor;

	@JsonAnyGetter
	@JsonIgnore
	private Map<String, Object> dynamicFields;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;
}
