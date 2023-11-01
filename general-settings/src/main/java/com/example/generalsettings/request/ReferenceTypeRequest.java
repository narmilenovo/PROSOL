package com.example.generalsettings.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceTypeRequest {
	 	private String referenceTypeCode;
	    private String referenceTypeName;
	    private Boolean referenceTypeStatus;
	    private Boolean duplicateCheck;
}
