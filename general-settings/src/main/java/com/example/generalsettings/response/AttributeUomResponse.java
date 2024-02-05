package com.example.generalsettings.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeUomResponse {
	private Long id;
	private String attributeUomName;
	private String attributeUomUnit;
	private Boolean attributeUomStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
