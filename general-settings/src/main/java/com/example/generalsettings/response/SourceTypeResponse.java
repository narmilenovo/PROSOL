package com.example.generalsettings.response;

import lombok.Data;

@Data
public class SourceTypeResponse {
	  	private Long id;
	    private String sourceTypeCode;
	    private String sourceTypeName;
	    private Boolean sourceTypeStatus;
}
