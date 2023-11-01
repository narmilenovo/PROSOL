package com.example.generalsettings.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceTypeRequest {
	 	private String sourceTypeCode;
	    private String sourceTypeName;
	    private Boolean sourceTypeStatus;
}
