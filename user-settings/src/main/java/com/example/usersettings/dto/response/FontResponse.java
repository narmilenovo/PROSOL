package com.example.usersettings.dto.response;

import java.util.Date;
import java.util.List;

import com.example.usersettings.entity.UpdateAuditHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontResponse {

	private Long id;

	private String fontName;

	private List<FontPropertyResponse> fontProperties;

	private ThemeResponse theme;

	private String createdBy;
	private Date createdAt;
	private List<UpdateAuditHistory> updateAuditHistories;

}
