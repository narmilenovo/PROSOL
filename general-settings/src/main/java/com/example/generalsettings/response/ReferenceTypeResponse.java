package com.example.generalsettings.response;

import lombok.Data;

@Data
public class ReferenceTypeResponse {
	private Long id;
    private String referenceTypeCode;
    private String referenceTypeName;
    private Boolean referenceTypeStatus;
    private Boolean duplicateCheck;
}
