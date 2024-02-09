package com.example.valueservice.client.GeneralSetting;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeUom {
	private Long id;
	private String attributeUomName;
	private String attributeUomUnit;
	private Boolean attributeUomStatus;
	private String createdBy;
	private String updatedBy;
	private Date createdAt;
	private Date updatedAt;
}
