package com.example.generalservice.dto.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlternateUOMResponse {
	private Long id;
	private String uomCode;
	private String uomName;
	private Boolean uomStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}